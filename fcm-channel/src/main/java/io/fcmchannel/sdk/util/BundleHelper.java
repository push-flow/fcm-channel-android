package io.fcmchannel.sdk.util;

import android.os.Bundle;

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

    public static Integer getMessageId(Bundle data) {
        return Integer.valueOf(data.getString(EXTRA_MESSAGE_ID, "0"));
    }

    public static String getMessageText(Bundle data) {
        return data.getString(EXTRA_MESSAGE);
    }

    public static MessageMetadata getMessageMetadata(Bundle data) {
        return data.getParcelable(EXTRA_METADATA);
    }

    public static Message getMessage(Bundle data) {
        Message message = new Message();
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

}
