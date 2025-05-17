package ntfyclient.exceptions;

public class NtfyConnectionException extends NtfyException {

	public NtfyConnectionException(String message) {
		super(message);
	}
	
	public NtfyConnectionException(String message, Exception e) {
		super(message, e);
	}
}
