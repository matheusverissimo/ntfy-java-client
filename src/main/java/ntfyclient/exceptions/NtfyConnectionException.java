package ntfyclient.exceptions;

public class NtfyConnectionException extends RuntimeException {

	public NtfyConnectionException(String message, Exception e) {
		super(message, e);
	}
}
