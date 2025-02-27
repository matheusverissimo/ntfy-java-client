package ntfyclient.models;

public class ViewAction extends Action {

	private String label;
	private String url;
	private Boolean clear;
	
	public ViewAction() {
		super("view");
	}
	
	public ViewAction label(String label) {
		this.label = label;
		return this;
	}
	
	public ViewAction url(String url) {
		this.url = url;
		return this;
	}
	
	public ViewAction clear(Boolean clear) {
		this.clear = clear;
		return this;
	}

	public String getLabel() {
		return label;
	}

	public String getUrl() {
		return url;
	}

	public Boolean getClear() {
		return clear;
	}
	
	
	
}
