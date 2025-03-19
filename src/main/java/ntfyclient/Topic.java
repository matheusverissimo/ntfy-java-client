package ntfyclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
		HttpRequest req = httpReqFromNotificationRequest(request);
		NotificationResponse res = null;
		try {
			logger.debug("Notifying topic {}.", topicName);
			HttpResponse<String> response = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
			res = gson.fromJson(response.body(), NotificationResponse.class);
			logger.atDebug().setMessage("Response: {}").addArgument(() -> response.body()).log();
		} catch (IOException | InterruptedException e) {
			throw new NtfyConnectionException("Error connecting to ntfy server.", e);
		}
		return res;
	}

	/**
	 * Asynchronously publish the notification on the topic.
	 * 
	 * @param request  The object that represents the notification to be send
	 * @param consumer An consumer function that processes the notification
	 *                 response.
	 */
	public void notifyAsync(NotificationRequest request, Consumer<NotificationResponse> consumer) {
		HttpRequest req = httpReqFromNotificationRequest(request);
		httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenApply(r -> r.body())
				.thenApply(json -> gson.fromJson(json, NotificationResponse.class)).thenAccept(consumer);
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

	/**
	 * This function blocks indefinitely, opening the connection to the ntfy.sh
	 * server and listening to the topic.
	 */
	public void listen() {
		logger.debug("Listening to {}", url + topicName + "/json");
		HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url + topicName + "/json")).GET().build();
		CompletableFuture<Void> future = httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofLines())
				.thenAccept(stream -> stream.body().forEach(json -> {
					logger.atDebug().setMessage("{}").addArgument(() -> json).log();
					Notification notification = gson.fromJson(json, Notification.class);
					if (notification.getEvent().equals("message"))
						subscribers.forEach(s -> s.accept(notification));
				}));
		future.join();
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
