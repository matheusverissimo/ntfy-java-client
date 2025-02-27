package ntfyclient.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Attachment {

	private String name;
	private String url;
	private String type;
	private Long size;
	private String expires;

	public String getName() {
		return name;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getType() {
		return type;
	}
	
	public Long getSize() {
		return size;
	}
	
	public LocalDateTime getExpires() {
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.valueOf(expires)), ZoneOffset.UTC);
	}
}
