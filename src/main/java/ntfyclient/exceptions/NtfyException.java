package ntfyclient.exceptions;

public class NtfyException extends RuntimeException {

	public NtfyException(String message) {
		super(message);
	}
	
	public NtfyException(String message, Exception e) {
		super(message, e);
	}
}
