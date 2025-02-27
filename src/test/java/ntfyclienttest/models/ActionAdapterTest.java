package ntfyclienttest.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ntfyclient.models.Action;
import ntfyclient.models.ActionAdapter;
import ntfyclient.models.BroadcastAction;
import ntfyclient.models.HttpAction;
import ntfyclient.models.NotificationRequest;
import ntfyclient.models.NotificationResponse;
import ntfyclient.models.ViewAction;

public class ActionAdapterTest {

	static Gson gson;
	
	@BeforeAll
	static void setup() {
		gson = new GsonBuilder().registerTypeAdapter(Action.class, new ActionAdapter()).create();
	}
	
	@Test
	void testDeserialization() {
		String json = "{\"id\":\"7lZQHtMyOaQH\",\"time\":1740667752,\"expires\":1740710952,\"event\":\"message\","
				+ "\"topic\":\"ntfy-java-client-test\",\"title\":\"Test message!\",\"message\":\"# Testing java-ntfy-client!\\n"
				+ " ## Don't worry this is a **test**.\",\"priority\":3,\"tags\":[\"hammer_and_wrench\"],\"click\":\"https://ntfy.sh\","
				+ "\"actions\":[{\"id\":\"3alQsYfylZ\",\"action\":\"view\",\"label\":\"What is pub-sub?\",\"clear\":false,"
				+ "\"url\":\"https://en.wikipedia.org/wiki/Publish-subscribe_pattern\"},{\"id\":\"hq8dBliIbH\",\"action\":\"broadcast\","
				+ "\"label\":\"What is pub-sub?\",\"clear\":false,\"url\":\"https://en.wikipedia.org/wiki/Publish-subscribe_pattern\","
				+ "\"extras\":{\"key\":\"value\",\"key2\":\"value2\"}},{\"id\":\"2EvDOjh55O\",\"action\":\"http\",\"label\":\"What is pub-sub?\","
				+ "\"clear\":false,\"url\":\"https://en.wikipedia.org/wiki/Publish-subscribe_pattern\",\"headers\":{\"Content-Type\":\"application/json\"}}],"
				+ "\"content_type\":\"text/markdown\"}";
		NotificationResponse res = gson.fromJson(json, NotificationResponse.class);
		List<Action> actions = res.getActions();
		for (Action action : actions) {
			switch (action.getAction()) {
			case "view":
				assertTrue(action instanceof ViewAction);
				ViewAction viewAction = (ViewAction) action;
				assertNotNull(viewAction.getUrl());
				break;
			case "broadcast":
				assertTrue(action instanceof BroadcastAction);
				BroadcastAction broadcastAction = (BroadcastAction) action;
				assertEquals(broadcastAction.getExtras().get("key2"), "value2");
				break;
			case "http":
				assertTrue(action instanceof HttpAction);
				HttpAction httpAction = (HttpAction) action;
				assertEquals(httpAction.getHeaders().get("Content-Type"), "application/json");
				break;
			default:
				break;
			}
		}
	}
	
	@Test
	void testSerialization() {
		NotificationRequest req = new NotificationRequest().addAction(new ViewAction().label("What is pub-sub?")
				.url("https://en.wikipedia.org/wiki/Publish-subscribe_pattern"))
		.addAction(new BroadcastAction().label("What is pub-sub?")
				.url("https://en.wikipedia.org/wiki/Publish-subscribe_pattern").addExtra("key", "value")
				.addExtra("key2", "value2"))
		.addAction(new HttpAction().label("What is pub-sub?")
				.url("https://en.wikipedia.org/wiki/Publish-subscribe_pattern")
				.addHeader("Content-Type", "application/json"));
		JsonObject jsonObj = gson.toJsonTree(req).getAsJsonObject();
		JsonArray actionsArray = jsonObj.get("actions").getAsJsonArray();
		assertEquals(actionsArray.size(), 3);
		
		for(JsonElement actionElement : actionsArray) {			
			JsonObject actionObj = actionElement.getAsJsonObject();
			String actionString = actionObj.get("action").getAsString();
			switch (actionString) {
			case "view":
				assertTrue(actionObj.get("url").getAsString().contains("wiki"));
				break;
			case "broadcast":
				assertEquals(actionObj.get("extras").getAsJsonObject().size(), 2);
				break;
			case "http":
				assertEquals(actionObj.get("headers").getAsJsonObject().size(), 1);
				break;
			default:
				break;
			}
			
		}
	}
}
