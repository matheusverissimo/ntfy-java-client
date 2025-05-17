package ntfyclient.models;

/**
 * Defines the priority level of the notification
 */
public enum Priority {

	MAX(5),
	HIGH(4),
	DEFAULT(3),
	LOW(2),
	MIN(1);
	
	Integer priorityCode;
	
	Priority(int priorityCode){
		this.priorityCode = priorityCode;
	}
	
	Integer getPriorityCode() {
		return this.priorityCode;
	}
}
