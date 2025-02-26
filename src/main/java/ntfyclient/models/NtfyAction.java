package ntfyclient.models;

abstract class NtfyAction {

	private String action;
	
	protected NtfyAction(String action) {
		this.action = action;
	}
}
