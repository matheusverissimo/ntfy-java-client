package ntfyclient.models;

public class Action {
	
	private String action;
	
	Action(String action) {
		this.action = action;
	}
	
	public String getAction() {
		return action;
	}
	
	void action(String action) {
		this.action = action;
	}
}
