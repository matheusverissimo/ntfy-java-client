package ntfyclient.models;

public class NtfyViewAction extends NtfyAction {

	private String label;
	private String url;
	private Boolean clear;
	
	public NtfyViewAction() {
		super("view");
	}
	
	public NtfyViewAction label(String label) {
		this.label = label;
		return this;
	}
	
	public NtfyViewAction url(String url) {
		this.url = url;
		return this;
	}
	
	public NtfyViewAction clear(Boolean clear) {
		this.clear = clear;
		return this;
	}
	
}
