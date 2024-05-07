package lca.Utilities;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import lca.epdConnectors.JsonWrappers.AniesJsonObject;

public class CustomDeserializerAnies implements JsonDeserializer<AniesJsonObject> {
    @Override
    public AniesJsonObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String module = "";
        String value = "";
        if (jsonObject.get("module") != null && jsonObject.get("value") != null) {
            module = jsonObject.get("module").getAsString();
            value = jsonObject.get("value").getAsString();
        }
        
        return new AniesJsonObject(value, module);
    }
}
