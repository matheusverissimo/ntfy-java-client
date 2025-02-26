package ntfyclient.models;

import java.util.ArrayList;
import java.util.List;

import ntfyclient.constants.NtfyPriority;

public class NtfyNotificationRequest {

	private String topic;
	private String message;
	private String title;
	private List<String> tags = new ArrayList<>();
	private Integer priority = NtfyPriority.DEFAULT;
	private String attach;
	private String filename;
	private List<NtfyAction> actions = new ArrayList<>();
	private String click;
	private Boolean markdown;
	private String icon;
	private String delay;
	private String email;
	private String call;

	public NtfyNotificationRequest topic(String topic) {
		this.topic = topic;
		return this;
	}

	public NtfyNotificationRequest message(String message) {
		this.message = message;
		return this;
	}

	public NtfyNotificationRequest title(String title) {
		this.title = title;
		return this;
	}

	public NtfyNotificationRequest tags(List<String> tags) {
		this.tags = tags;
		return this;
	}
	
	public NtfyNotificationRequest addTag(String tag) {
		this.tags.add(tag);
		return this;
	}

	public NtfyNotificationRequest priority(Integer priority) {
		this.priority = priority;
		return this;
	}

	public NtfyNotificationRequest attach(String attachUrl) {
		this.attach = attachUrl;
		return this;
	}

	public NtfyNotificationRequest filename(String filename) {
		this.filename = filename;
		return this;
	}

	public NtfyNotificationRequest actions(List<NtfyAction> actions) {
		this.actions = actions;
		return this;
	}
	
	public NtfyNotificationRequest addAction(NtfyAction action) {
		this.actions.add(action);
		return this;
	}
	
	public NtfyNotificationRequest click(String clickUrl) {
		this.click = clickUrl;
		return this;
	}

	public NtfyNotificationRequest markdown(Boolean markdown) {
		this.markdown = markdown;
		return this;
	}

	public NtfyNotificationRequest icon(String iconUrl) {
		this.icon = iconUrl;
		return this;
	}

	public NtfyNotificationRequest delay(String delay) {
		this.delay = delay;
		return this;
	}

	public NtfyNotificationRequest email(String email) {
		this.email = email;
		return this;
	}
	
	public NtfyNotificationRequest call(String call) {
		this.call = call;
		return this;
	}
}
