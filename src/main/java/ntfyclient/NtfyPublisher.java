package ntfyclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ntfyclient.constants.NtfyConstants;
import ntfyclient.exceptions.NtfyConnectionException;
import ntfyclient.models.NtfyNotificationRequest;
import ntfyclient.models.NtfyNotificationResponse;

public class NtfyPublisher {

	private static final Logger logger = LoggerFactory.getLogger(NtfyPublisher.class);

	private final String topic;
	private final String url;
	private final HttpClient httpClient = HttpClient.newHttpClient();

	public NtfyPublisher(String topic) {
		this.topic = topic;
		this.url = NtfyConstants.NTFY_URL;
	}

	public NtfyPublisher(String url, String topic) {
		this.url = url;
		this.topic = topic;
	}

	public NtfyNotificationResponse notify(NtfyNotificationRequest request) {
		request.topic(topic);
		Gson gson = new Gson();
		String body = gson.toJson(request);
		BodyPublisher bp = HttpRequest.BodyPublishers.ofString(body);
		HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).POST(bp).build();
		NtfyNotificationResponse res = null;
		try {
			logger.debug("Publishing to {}. Body: {}", url, body);
			HttpResponse<String> response = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
			logger.debug("{} response: {} - {}", url, response.statusCode(), response.body());
			Map<String, Object> map = gson.fromJson(response.body(), Map.class);
			res = new NtfyNotificationResponse((String) map.get("id"), (String) map.get("time"),
					(String) map.get("expires"), request);
		} catch (IOException | InterruptedException e) {
			throw new NtfyConnectionException("Error connecting to ntfy server.", e);
		}
		return res;
	}

	public CompletableFuture<NtfyNotificationResponse> notifyAsync(NtfyNotificationRequest request, Consumer<NtfyNotificationResponse> consumer) {
		request.topic(topic);
		Gson gson = new Gson();
		String body = gson.toJson(request);
		BodyPublisher bp = HttpRequest.BodyPublishers.ofString(body);
		HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).POST(bp).build();
		logger.debug("Publishing to {}. Body: {}", url, body);
		return httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenApply(r -> r.body())
				.thenApply(json -> gson.fromJson(json, Map.class))
				.thenApply(map -> new NtfyNotificationResponse((String) map.get("id"), 
						(String) map.get("time"),
						(String) map.get("expires"), request)
						);
	}
}
