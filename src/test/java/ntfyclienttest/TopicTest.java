package ntfyclienttest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ntfyclient.Topic;
import ntfyclient.models.BroadcastAction;
import ntfyclient.models.HttpAction;
import ntfyclient.models.NotificationRequest;
import ntfyclient.models.NotificationResponse;
import ntfyclient.models.ViewAction;

public class TopicTest {

	static final Logger log = LoggerFactory.getLogger(TopicTest.class);

	@Test
	void testNotifySync() {
		Topic topic = new Topic("ntfy-java-client-test");
		NotificationRequest req = new NotificationRequest().markdown(true).title("Test message!")
				.message("# Testing java-ntfy-client!\n ## Don't worry this is a **test**.")
				.addAction(new ViewAction().label("What is pub-sub?")
						.url("https://en.wikipedia.org/wiki/Publish-subscribe_pattern"))
				.addAction(new BroadcastAction().label("What is pub-sub?")
						.url("https://en.wikipedia.org/wiki/Publish-subscribe_pattern").addExtra("key", "value")
						.addExtra("key2", "value2"))
				.addAction(new HttpAction().label("What is pub-sub?")
						.url("https://en.wikipedia.org/wiki/Publish-subscribe_pattern")
						.addHeader("Content-Type", "application/json"))
				.addTag("hammer_and_wrench").click("https://ntfy.sh");
		NotificationResponse response = topic.notify(req);
		assertNotNull(response.getId());
	}
	
	@Test
	void testNotifyAsync() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		Topic topic = new Topic("ntfy-java-client-test");
		NotificationRequest req = new NotificationRequest().markdown(true).title("Test message!")
				.message("# Testing java-ntfy-client!\n ## Don't worry this is a **test**.")
				.addAction(new ViewAction().label("What is pub-sub?")
						.url("https://en.wikipedia.org/wiki/Publish-subscribe_pattern"))
				.addAction(new BroadcastAction().label("What is pub-sub?")
						.url("https://en.wikipedia.org/wiki/Publish-subscribe_pattern").addExtra("key", "value")
						.addExtra("key2", "value2"))
				.addAction(new HttpAction().label("What is pub-sub?")
						.url("https://en.wikipedia.org/wiki/Publish-subscribe_pattern")
						.addHeader("Content-Type", "application/json"))
				.addTag("hammer_and_wrench").click("https://ntfy.sh");
		topic.notifyAsync(req, nr -> {
			latch.countDown();
		});
		
		Boolean success = latch.await(5, TimeUnit.SECONDS);
		
		assertTrue(success, "The notification took too long.");
	}
	
	@Test
	void listenToTopic() throws InterruptedException {
		String[] receivedMsg = new String[1];
		CountDownLatch latch = new CountDownLatch(1);
		Topic topic = new Topic("ntfy-java-client-test");
		topic.subscribe(notification -> {
			receivedMsg[0] = notification.getMessage();
			log.debug("Message received: {}", notification.getMessage());
			latch.countDown();
		});
		
		new Thread(() -> topic.listen()).start();
		
		Thread.sleep(2000);
		topic.notify(new NotificationRequest().message("Test ok"));
		
		latch.await(5, TimeUnit.SECONDS);
		
		assertEquals("Test ok", receivedMsg[0]);
	}
}
