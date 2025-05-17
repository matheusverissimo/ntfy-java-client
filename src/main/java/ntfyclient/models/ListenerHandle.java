package ntfyclient.models;

import java.util.concurrent.atomic.AtomicBoolean;

public class ListenerHandle {
	
	private final AtomicBoolean running = new AtomicBoolean(true);

	public void stop() {
		running.set(false);;
	}
	
	public boolean isRunning() {
		return running.get();
	}
}
