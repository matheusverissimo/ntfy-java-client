package ntfyclient.models;

import java.util.HashMap;
import java.util.Map;

public class HttpAction extends Action {

	private String label;
	private String url;
	private String method;
	private Map<String, String> headers = new HashMap<>();
	private String body;
	private Boolean clear;
	
	public HttpAction() {
		super("http");
	}
	
	public HttpAction label(String label) {
		this.label = label;
		return this;
	}
	
	public HttpAction url(String url) {
		this.url = url;
		return this;
	}
	
	public HttpAction headers(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}
	
	public HttpAction addHeader(String header, String value) {
		this.headers.put(header, value);
		return this;
	}

	public String getLabel() {
		return label;
	}

	public String getUrl() {
		return url;
	}

	public String getMethod() {
		return method;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getBody() {
		return body;
	}

	public Boolean getClear() {
		return clear;
	}
	
	
}
