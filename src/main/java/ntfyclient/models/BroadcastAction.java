package ntfyclient.models;

import java.util.HashMap;
import java.util.Map;

public class BroadcastAction extends Action {

	private String label;
	private String url;
	private Map<String, String> extras = new HashMap<String, String>(5);
	private Boolean clear;
	
	public BroadcastAction() {
		super("broadcast");
	}
	
	public BroadcastAction label(String label) {
		this.label = label;
		return this;
	}
	
	public BroadcastAction url(String url) {
		this.url = url;
		return this;
	}
	
	public BroadcastAction extras(Map<String, String> extras) {
		this.extras = extras;
		return this;
	}
	
	public BroadcastAction addExtra(String key, String value) {
		this.extras.put(key, value);
		return this;
	}
	
	public BroadcastAction clear(Boolean clear) {
		this.clear = clear;
		return this;
	}

	public String getLabel() {
		return label;
	}

	public String getUrl() {
		return url;
	}

	public Map<String, String> getExtras() {
		return extras;
	}

	public Boolean getClear() {
		return clear;
	}
	
	

}
