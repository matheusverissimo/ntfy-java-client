package ntfyclient.models;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ActionAdapter extends TypeAdapter<Action> {


    private final Gson gson = new Gson();

    @Override
    public void write(JsonWriter out, Action value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        JsonObject jsonObject = gson.toJsonTree(value).getAsJsonObject();
        jsonObject.addProperty("action", value.getAction());

        gson.toJson(jsonObject, out);
    }

    @Override
    public Action read(JsonReader in) throws IOException {
        JsonObject jsonObject = JsonParser.parseReader(in).getAsJsonObject();

        JsonElement typeElement = jsonObject.get("action");
        if (typeElement == null) {
            throw new JsonParseException("Field 'action' is missing on Json.");
        }

        String actionType = typeElement.getAsString();
        Action action;

        switch (actionType) {
            case "view":
                action = gson.fromJson(jsonObject, ViewAction.class);
                break;
            case "broadcast":
                action = gson.fromJson(jsonObject, BroadcastAction.class);
                break;
            case "http":
                action = gson.fromJson(jsonObject, HttpAction.class);
                break;
            default:
                throw new JsonParseException("Unknown action type: " + actionType);
        }

        action.action(actionType);
        return action;
    }

}
