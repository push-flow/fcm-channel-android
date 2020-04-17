package io.fcmchannel.sdk.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.fcmchannel.sdk.FcmClient;
import io.fcmchannel.sdk.R;
import io.fcmchannel.sdk.chat.FcmClientChatActivity;
import io.fcmchannel.sdk.chat.menu.FcmClientMenuService;
import io.fcmchannel.sdk.core.models.Message;
import io.fcmchannel.sdk.persistence.Preferences;
import io.fcmchannel.sdk.util.BundleHelper;

/**
 * Created by john-mac on 6/29/16.
 */
public class FcmClientIntentService extends FirebaseMessagingService {

    private static List<Message> messagesCache;

    public static final String ACTION_MESSAGE_RECEIVED = "io.fcmchannel.sdk.MESSAGE_RECEIVED";
    private static final String CUSTOM_TYPE = "rapidpro";

    private static final int NOTIFICATION_ID = 30;
    private static final String NOTIFICATION_CHANNEL_ID = "fcm_client_chat";

    public static final String KEY_DATA = "data";
    private static final String KEY_TYPE = "type";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TITLE = "title";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        FcmClient.refreshContactToken();
    }

    @Override
    @CallSuper
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        String type = data.get(KEY_TYPE);

        if (isRapidproType(type) && FcmClient.isContactRegistered()) {
            Intent pushReceiveIntent = new Intent(ACTION_MESSAGE_RECEIVED);
            Bundle bundle = BundleHelper.convertToBundleFrom(data);
            pushReceiveIntent.putExtra(KEY_DATA, bundle);

            Message message = BundleHelper.getMessage(bundle);
            message.setCreatedOn(new Date());
            addMessageToCache(message);

            if (!FcmClient.isChatVisible()) {
                int unreadMessages = increaseUnreadMessages();
                if (!FcmClientMenuService.isVisible()) {
                    showFloatingMenu(unreadMessages);
                }
                showLocalNotification(data.get(KEY_TITLE), data.get(KEY_MESSAGE));
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushReceiveIntent);
        }
    }

    public static List<Message> getMessagesCache() {
        if (messagesCache == null) {
            messagesCache = new ArrayList<>();
        }
        return messagesCache;
    }

    public static void addMessageToCache(Message message) {
        if (messagesCache == null) {
            messagesCache = new ArrayList<>();
        }
        messagesCache.add(message);
    }

    private void showFloatingMenu(int unreadMessages) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                FcmClientMenuService.showFloatingMenu(getApplicationContext(), unreadMessages);
            }
        } else {
            FcmClientMenuService.showFloatingMenu(getApplicationContext(), unreadMessages);
        }
    }

    private int increaseUnreadMessages() {
        Preferences preferences = FcmClient.getPreferences();
        int newUnreadMessages = preferences.getUnreadMessages() + 1;
        preferences.setUnreadMessages(newUnreadMessages);
        preferences.commit();

        return newUnreadMessages;
    }

    private void showLocalNotification(String title, String message) {
        message = handleNotificationMessage(message);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(getApplicationInfo().icon)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setAutoCancel(true);
        mBuilder.setContentIntent(createPendingIntent());
        onCreateLocalNotification(mBuilder);
    }

    protected void onCreateLocalNotification(NotificationCompat.Builder builder) {
        NotificationManager mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    getString(R.string.notification_channel),
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);

            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private String handleNotificationMessage(String message) {
        return !TextUtils.isEmpty(message) ? Html.fromHtml(message).toString() : message;
    }

    private boolean isRapidproType(String type) {
        return type != null && type.equals(CUSTOM_TYPE);
    }

    private PendingIntent createPendingIntent() {
        Intent chatIntent = new Intent(this, FcmClientChatActivity.class);
        chatIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(this, 0, chatIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
