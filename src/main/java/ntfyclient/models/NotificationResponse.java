package ntfyclient.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class NotificationResponse {

	private String id;
	private String time;
	private String expires;
	private String event;
	private String topic;
	private String title;
	private String message;
	private Integer priority;
	private List<String> tags;
	private String click;
	private List<Action> actions;
	private String contentType;

	public String getId() {
		return id;
	}

	public LocalDateTime getTime() {
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.valueOf(time)), ZoneOffset.UTC);
	}

	public LocalDateTime getExpires() {
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.valueOf(expires)), ZoneOffset.UTC);
	}

	public String getEvent() {
		return event;
	}

	public String getTopic() {
		return topic;
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public Integer getPriority() {
		return priority;
	}

	public List<String> getTags() {
		return tags;
	}

	public String getClick() {
		return click;
	}

	public List<Action> getActions() {
		return actions;
	}

	public String getContentType() {
		return contentType;
	}

	
	
}
