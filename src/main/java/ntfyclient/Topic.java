package ntfyclient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import ntfyclient.exceptions.NtfyConnectionException;
import ntfyclient.exceptions.NtfyException;
import ntfyclient.models.Action;
import ntfyclient.models.ActionAdapter;
import ntfyclient.models.ListenerHandle;
import ntfyclient.models.Notification;
import ntfyclient.models.NotificationRequest;
import ntfyclient.models.NotificationResponse;

/**
 * This class represents a ntfy.sh topic, can be used to listen to notifications
 * and to notify.
 */
public class Topic {

	private static final Logger logger = LoggerFactory.getLogger(Topic.class);

	private final String topicName;
	private final String url;
	private final HttpClient httpClient = HttpClient.newHttpClient();
	private final Gson gson = new GsonBuilder().registerTypeAdapter(Action.class, new ActionAdapter()).create();
	private final List<Consumer<Notification>> subscribers = new ArrayList<>();

	/**
	 * This class represents a ntfy.sh topic, can be used to listen to notifications
	 * and to notify. Using this constructor the connection is established to the
	 * main ntfy.sh server.
	 * 
	 * @param topicName The name of the topic.
	 */
	public Topic(String topicName) {
		this(NtfyConstants.NTFY_URL, topicName);
	}

	/**
	 * This class represents a ntfy.sh topic, can be used to listen to notifications
	 * and to notify. Using this constructor the connection is established to the
	 * provided server
	 * 
	 * @param url       The URL of the ntfy.sh server
	 * @param topicName The name of the topic.
	 */
	public Topic(String url, String topicName) {
		if (topicName == null || topicName.isBlank())
			throw new NtfyException("Topic cannot be empty");
		if (url == null || url.isBlank())
			throw new NtfyException("URL cannot be empty");
		if (!url.endsWith("/"))
			url = url + "/";
		this.topicName = topicName;
		this.url = url;
	}

	/**
	 * Synchronously publish the notification on the topic.
	 * 
	 * @param request The object that represents the notification to be send
	 * @return The object that contains information about the sent notification
	 */
	public NotificationResponse notify(NotificationRequest request) {
		try {
			return notifyAsync(request).get(); // Block until result is available
		} catch (InterruptedException | ExecutionException e) {
			throw new NtfyConnectionException("Error connecting to ntfy server.", e);
		}
	}

	/**
	 * Asynchronously publish the notification on the topic.
	 * 
	 * @param request The object that represents the notification to be send
	 * @return A CompletableFuture with the notification response object.
	 */
	public CompletableFuture<NotificationResponse> notifyAsync(NotificationRequest request) {
		HttpRequest req = httpReqFromNotificationRequest(request);
		logger.debug("Notifying topic {}.", topicName);
		return httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenApply(r -> r.body())
				.thenApply(json -> {
					logger.atDebug().setMessage("Response: {}").addArgument(() -> json).log();
					return gson.fromJson(json, NotificationResponse.class);
				});
	}

	/**
	 * Asynchronously publish the notification and handle result with a consumer.
	 * 
	 * @param request  The object that represents the notification to be send
	 * @param consumer A consumer function that processes the notification response.
	 */
	public void notifyAsync(NotificationRequest request, Consumer<NotificationResponse> consumer) {
		notifyAsync(request).thenAccept(consumer);
	}

	/**
	 * Add an subscriber function to the topic, that will execute when an
	 * notification is received on it.
	 * 
	 * @param subscription The function that will be executed when an notification
	 *                     is received.
	 */
	public void subscribe(Consumer<Notification> subscription) {
		subscribers.add(subscription);
	}
	
	private Runnable listenerRunnable(ListenerHandle handle, Integer maxRetries, final int baseDelay) {
		return () -> {
			int retryCount = 0;
			
			while (handle.isRunning()) {
				try {
					logger.debug("Listening to {}", url + topicName + "/json");
					HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url + topicName + "/json")).GET().build();

					httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofLines())
							.thenAccept(response -> response.body().forEach(json -> {
								if (!handle.isRunning())
									return;
								logger.atDebug().setMessage("Received message: {}").addArgument(() -> json).log();
								Notification notification = gson.fromJson(json, Notification.class);
								if ("message".equals(notification.getEvent()))
									subscribers.forEach(s -> s.accept(notification));
							})).join();

					retryCount = 0;
				} catch (Exception e) {
					if (!handle.isRunning()) 
						break;

					retryCount++;
					if (retryCount > maxRetries) {
						logger.error("Max retry attempts reached. Stopping listener.", e);
						break;
					}

					int backoff = baseDelay * (1 << (retryCount - 1));
					logger.warn("Listener failed, retrying in {}ms (attempt {}/{}).", backoff, retryCount, maxRetries);
					
					try {
						Thread.sleep(backoff);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						break;
					}
				}
			}
		};
	}

	/**
	 * This method starts a listener thread using the ExecutorService provided, opening the connection to the ntfy.sh
	 * server and listening to the topic.
	 * 
	 * @param executorService The ExecutorService in which the listener thread will be submitted.
	 * @param maxRetries In case of errors on the connection on the server, the maximum number of retries.
	 * @param baseDelay The base delay in milliseconds, on the exponential backoff time that will be applied on retries.
	 * 
	 * @return A handle that can signal cancellation to the listener thread. 
	 */
	public ListenerHandle listen(ExecutorService executorService, int maxRetries, int baseDelay) {
		ListenerHandle handle = new ListenerHandle();
		executorService.submit(listenerRunnable(handle, maxRetries, baseDelay));
		return handle;
	}
	
	/**
	 * This method starts a listener thread using the Executors.newSingleThreadExecutor(), opening the connection to the ntfy.sh
	 * server and listening to the topic.
	 * 
	 * @param maxRetries In case of errors on the connection on the server, the maximum number of retries.
	 * @param baseDelay The base delay in milliseconds, on the exponential backoff time that will be applied on retries.
	 * 
	 * @return A handle that can signal cancellation to the listener thread. 
	 */
	public ListenerHandle listen(int maxRetries, int baseDelay) {
		ListenerHandle handle = new ListenerHandle();
		Executors.newSingleThreadExecutor().submit(listenerRunnable(handle, maxRetries, baseDelay));
		return handle;
	}
	
	/**
	 * This method starts a listener thread using the Executors.newSingleThreadExecutor(), opening the connection to the ntfy.sh
	 * server and listening to the topic. 
	 * In case of errors in the listening connection, an exponential backoff will be applied, being 5 the maximum number of retries 
	 * with 2 seconds of base delay.
	 * 
	 * @return A handle that can signal cancellation to the listener thread. 
	 */
	public ListenerHandle listen() {
		ListenerHandle handle = new ListenerHandle();
		Executors.newSingleThreadExecutor().submit(listenerRunnable(handle, 5, 2000));
		return handle;
	}
	
	/**
	 * This method starts a listener thread using the Executors.newSingleThreadExecutor(), opening the connection to the ntfy.sh
	 * server and listening to the topic. 
	 * In case of errors in the listening connection, an exponential backoff will be applied, being 5 the maximum number of retries 
	 * with 2 seconds of base delay.
	 */
	public void listenAndJoin() {
		ListenerHandle handle = new ListenerHandle();
		try {
			Executors.newSingleThreadExecutor().submit(listenerRunnable(handle, 5, 2000)).get();
		} catch (InterruptedException | ExecutionException e) {
			handle.stop();
			throw new NtfyConnectionException("An error occured during the listening to the topic.", e);
		}
	}

	private HttpRequest httpReqFromNotificationRequest(NotificationRequest request) {
		JsonElement jsonElement = gson.toJsonTree(request);
		jsonElement.getAsJsonObject().addProperty("topic", topicName);
		BodyPublisher bp = HttpRequest.BodyPublishers.ofString(gson.toJson(jsonElement));
		return HttpRequest.newBuilder().uri(URI.create(url)).POST(bp).build();
	}

	/**
	 * @return The server URL.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return The name of the topic.
	 */
	public String getTopicName() {
		return topicName;
	}
}
