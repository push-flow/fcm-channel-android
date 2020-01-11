package io.fcmchannel.sdk.util;

import android.os.Bundle;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.fcmchannel.sdk.core.models.Message;
import io.fcmchannel.sdk.core.models.MessageMetadata;

/**
 * Created by john-mac on 6/29/16.
 */
public class BundleHelper {

    private static final String EXTRA_MESSAGE_ID = "message_id";
    private static final String EXTRA_MESSAGE = "message";
    private static final String EXTRA_METADATA = "metadata";
    private static final String EXTRA_QUICK_REPLIES = "quick_replies";

    public static Message getMessage(Bundle data) {
        final Message message = new Message();
        message.setId(getMessageId(data));
        message.setText(getMessageText(data));
        message.setDirection(Message.DIRECTION_OUTGOING);
        message.setMetadata(getMessageMetadata(data));
        return message;
    }

    public static Bundle convertToBundleFrom(Map<String, String> data) {
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        return bundle;
    }

    private static Integer getMessageId(Bundle data) {
        return Integer.valueOf(data.getString(EXTRA_MESSAGE_ID, "0"));
    }

    private static String getMessageText(Bundle data) {
        return data.getString(EXTRA_MESSAGE);
    }

    private static MessageMetadata getMessageMetadata(Bundle data) {
        final Gson gson = new Gson();
        final String metadataJson = data.getString(EXTRA_METADATA);

        if (metadataJson != null && !metadataJson.equals("")) {
            return gson.fromJson(metadataJson, MessageMetadata.class);
        }
        final String quickRepliesJson = data.getString(EXTRA_QUICK_REPLIES);

        if (quickRepliesJson != null && !quickRepliesJson.isEmpty()) {
            final String[] array = gson.fromJson(quickRepliesJson, String[].class);
            final List<String> quickReplies = new ArrayList<>(Arrays.asList(array));
            final MessageMetadata metadata = new MessageMetadata();
            metadata.setQuickReply(quickReplies);

            return metadata;
        }
        return null;
    }

}
