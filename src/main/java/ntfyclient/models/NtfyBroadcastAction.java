package ntfyclient.models;

import java.util.HashMap;
import java.util.Map;

public class NtfyBroadcastAction extends NtfyAction {

	private String label;
	private String url;
	private Map<String, String> extras = new HashMap<String, String>(5);
	private Boolean clear;
	
	public NtfyBroadcastAction() {
		super("broadcast");
	}
	
	public NtfyBroadcastAction label(String label) {
		this.label = label;
		return this;
	}
	
	public NtfyBroadcastAction url(String url) {
		this.url = url;
		return this;
	}
	
	public NtfyBroadcastAction extras(Map<String, String> extras) {
		this.extras = extras;
		return this;
	}
	
	public NtfyBroadcastAction addExtra(String key, String value) {
		this.extras.put(key, value);
		return this;
	}
	
	public NtfyBroadcastAction clear(Boolean clear) {
		this.clear = clear;
		return this;
	}

}
