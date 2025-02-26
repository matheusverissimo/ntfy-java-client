package ntfyclient.models;

import java.util.HashMap;
import java.util.Map;

public class NtfyHttpAction extends NtfyAction {

	private String label;
	private String url;
	private String method;
	private Map<String, String> headers = new HashMap<>();
	private String body;
	private Boolean clear;
	
	public NtfyHttpAction() {
		super("http");
	}
	
	public NtfyHttpAction label(String label) {
		this.label = label;
		return this;
	}
	
	public NtfyHttpAction url(String url) {
		this.url = url;
		return this;
	}
	
	public NtfyHttpAction headers(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}
	
	public NtfyHttpAction addHeader(String header, String value) {
		this.headers.put(header, value);
		return this;
	}
}
