package io.fcmchannel.sdk.core.adapters;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import io.fcmchannel.sdk.core.models.MessageMetadata;

public class MetadataTypeAdapter implements JsonDeserializer<MessageMetadata> {
    @Override
    public MessageMetadata deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        MessageMetadata metadata = null;
        if(json.isJsonObject()) {
            Gson gson = new Gson();
            metadata = gson.fromJson(json, MessageMetadata.class);
        }
        return metadata;
    }
}
