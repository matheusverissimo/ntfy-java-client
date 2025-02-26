import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import ntfyclient.NtfyPublisher;
import ntfyclient.models.NtfyNotificationRequest;
import ntfyclient.models.NtfyNotificationResponse;
import ntfyclient.models.NtfyViewAction;

public class NtfyPublisherTest {

	@Test
	void testPublisher() {
		NtfyPublisher ntfyPublisher = new NtfyPublisher("ntfy-java-client-test");
		NtfyNotificationRequest req = new NtfyNotificationRequest().markdown(true).title("Test message!")
				.message("# Testing java-ntfy-client!\n ## Don't worry this is a **test**.")
				.addAction(new NtfyViewAction().label("What is pub-sub?")
						.url("https://en.wikipedia.org/wiki/Publish-subscribe_pattern"))
				.addTag("hammer_and_wrench").click("https://ntfy.sh");
		NtfyNotificationResponse response = ntfyPublisher.notify(req);
		assertNotNull(response.getId());
	}
}
