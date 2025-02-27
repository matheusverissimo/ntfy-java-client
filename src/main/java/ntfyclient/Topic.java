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

import ntfyclient.constants.NtfyConstants;
import ntfyclient.exceptions.NtfyConnectionException;
import ntfyclient.exceptions.NtfyException;
import ntfyclient.models.Action;
import ntfyclient.models.ActionAdapter;
import ntfyclient.models.Notification;
import ntfyclient.models.NotificationRequest;
import ntfyclient.models.NotificationResponse;

public class Topic {

	private static final Logger logger = LoggerFactory.getLogger(Topic.class);

	private final String topicName;
	private final String url;
	private final HttpClient httpClient = HttpClient.newHttpClient();
	private final Gson gson = new GsonBuilder().registerTypeAdapter(Action.class, new ActionAdapter()).create();
	private final List<Consumer<Notification>> subscribers = new ArrayList<>();

	public Topic(String topicName) {
		this(NtfyConstants.NTFY_URL, topicName);
	}

	public Topic(String url, String topicName) {
		if (topicName == null || topicName.isBlank())
			throw new NtfyException("Topic cannot be empty");
		if (url == null || url.isBlank())
			throw new NtfyException("URL cannot be empty");
		if(!url.endsWith("/"))
			url = url + "/";
		this.topicName = topicName;
		this.url = url;
	}

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

	public void notifyAsync(NotificationRequest request, Consumer<NotificationResponse> consumer) {
		HttpRequest req = httpReqFromNotificationRequest(request);
		httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenApply(r -> r.body())
				.thenApply(json -> gson.fromJson(json, NotificationResponse.class)).thenAccept(consumer);
	}

	public void subscribe(Consumer<Notification> subscription) {
		subscribers.add(subscription);
	}

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

	public String getUrl() {
		return url;
	}

	public String getTopicName() {
		return topicName;
	}
}
