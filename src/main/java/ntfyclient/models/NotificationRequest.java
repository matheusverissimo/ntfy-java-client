package ntfyclient.models;

import java.util.ArrayList;
import java.util.List;

import ntfyclient.Priority;

public class NotificationRequest {

	private String topic;
	private String message;
	private String title;
	private List<String> tags = new ArrayList<>();
	private Integer priority = Priority.DEFAULT;
	private String attach;
	private String filename;
	private List<Action> actions = new ArrayList<>();
	private String click;
	private Boolean markdown;
	private String icon;
	private String delay;
	private String email;
	private String call;

	private NotificationRequest topic(String topic) {
		this.topic = topic;
		return this;
	}

	public NotificationRequest message(String message) {
		this.message = message;
		return this;
	}

	public NotificationRequest title(String title) {
		this.title = title;
		return this;
	}

	public NotificationRequest tags(List<String> tags) {
		this.tags = tags;
		return this;
	}
	
	public NotificationRequest addTag(String tag) {
		this.tags.add(tag);
		return this;
	}

	public NotificationRequest priority(Integer priority) {
		this.priority = priority;
		return this;
	}

	public NotificationRequest attach(String attachUrl) {
		this.attach = attachUrl;
		return this;
	}

	public NotificationRequest filename(String filename) {
		this.filename = filename;
		return this;
	}

	public NotificationRequest actions(List<Action> actions) {
		this.actions = actions;
		return this;
	}
	
	public NotificationRequest addAction(Action action) {
		this.actions.add(action);
		return this;
	}
	
	public NotificationRequest click(String clickUrl) {
		this.click = clickUrl;
		return this;
	}

	public NotificationRequest markdown(Boolean markdown) {
		this.markdown = markdown;
		return this;
	}

	public NotificationRequest icon(String iconUrl) {
		this.icon = iconUrl;
		return this;
	}

	public NotificationRequest delay(String delay) {
		this.delay = delay;
		return this;
	}

	public NotificationRequest email(String email) {
		this.email = email;
		return this;
	}
	
	public NotificationRequest call(String call) {
		this.call = call;
		return this;
	}
}
