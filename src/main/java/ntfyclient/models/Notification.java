package ntfyclient.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class Notification {

	private String id;
	private String time;
	private String expires;
	private String topic;
	private String event;
	private String message;
	private String title;
	private List<String> tags = new ArrayList<>();
	private Priority priority = Priority.DEFAULT;
	private String click;
	private List<Action> actions = new ArrayList<>();
	private Attachment attachment;

	public String getId() {
		return id;
	}

	public LocalDateTime getTime() {
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.valueOf(time)), ZoneOffset.UTC);
	}

	public LocalDateTime getExpires() {
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.valueOf(expires)), ZoneOffset.UTC);
	}

	public String getTopic() {
		return topic;
	}

	public String getEvent() {
		return event;
	}

	public String getMessage() {
		return message;
	}

	public String getTitle() {
		return title;
	}

	public List<String> getTags() {
		return tags;
	}

	public Priority getPriority() {
		return priority;
	}

	public String getClick() {
		return click;
	}

	public List<Action> getActions() {
		return actions;
	}

	public Attachment getAttachment() {
		return attachment;
	}
}
