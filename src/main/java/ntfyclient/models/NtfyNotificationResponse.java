package ntfyclient.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class NtfyNotificationResponse {

	private String id;
	private LocalDateTime time;
	private LocalDateTime expires;
	private NtfyNotificationRequest request;
	
	public NtfyNotificationResponse(String id, String timeInSeconds, String expiresInSeconds, NtfyNotificationRequest request) {
		this.id = id;
		this.time = LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.valueOf(timeInSeconds)), ZoneOffset.UTC);
		this.expires = LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.valueOf(expiresInSeconds)), ZoneOffset.UTC);
		this.request = request;
	}

	public String getId() {
		return id;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public LocalDateTime getExpires() {
		return expires;
	}

	public NtfyNotificationRequest getRequest() {
		return request;
	}
	
	
}
