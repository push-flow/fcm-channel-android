package io.fcmchannel.sdk.persistence;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by john-mac on 6/27/16.
 */
public class Preferences {

    private static final String KEY_FLOATING_CHAT_ENABLED = "floatingChatEnabled";
    private static final String KEY_URN = "urn";
    private static final String KEY_CONTACT_UUID = "contactUuid";
    private static final String KEY_UNREAD_MESSAGES = "unreadMessages";
    private static final String KEY_QUICK_REPLIES = "quickReplies";

    private static final String PREFERENCES_NAME = "io.fcmchannel.sdk.preferences";

    private final SharedPreferences sharedPreferences;
    private final String prefix;

    private final Map<String, String> objects;

    public Preferences(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        this.prefix = context.getPackageName();
        this.objects = new HashMap<>();
    }

    public boolean isFloatingChatEnabled() {
        return Boolean.valueOf(sharedPreferences.getString(getKey(KEY_FLOATING_CHAT_ENABLED), "true"));
    }

    public Preferences setFloatingChatEnabled(boolean enabled) {
        this.objects.put(getKey(KEY_FLOATING_CHAT_ENABLED), String.valueOf(enabled));
        return this;
    }

    public String getContactUuid() {
        return sharedPreferences.getString(getKey(KEY_CONTACT_UUID), null);
    }

    public Preferences setContactUuid(String contactUuid) {
        this.objects.put(getKey(KEY_CONTACT_UUID), contactUuid);
        return this;
    }

    public Preferences setUrn(String urn) {
        this.objects.put(getKey(KEY_URN), urn);
        return this;
    }

    public String getUrn() {
        return sharedPreferences.getString(getKey(KEY_URN), null);
    }

    public int getUnreadMessages() {
        return Integer.valueOf(sharedPreferences.getString(getKey(KEY_UNREAD_MESSAGES), "0"));
    }

    public Preferences setUnreadMessages(int unreadMessages) {
        this.objects.put(getKey(KEY_UNREAD_MESSAGES), String.valueOf(unreadMessages));
        return this;
    }

    public Set<String> getQuickRepliesOfLastMessage() {
        return sharedPreferences.getStringSet(getKey(KEY_QUICK_REPLIES), new HashSet<String>());
    }

    public void setQuickRepliesOfLastMessage(final Set<String> quickReplies) {
        sharedPreferences.edit().putStringSet(getKey(KEY_QUICK_REPLIES), quickReplies).apply();
    }

    public void clear() {
        setContactUuid("");
        setUrn("");
        setUnreadMessages(0);
        commit();
    }

    @SuppressLint("ApplySharedPref")
    public void commit() {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        for (String key : objects.keySet()) {
            editor.putString(key, objects.get(key));
        }
        editor.commit();
    }

    public void apply() {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        for (String key : objects.keySet()) {
            editor.putString(key, objects.get(key));
        }
        editor.apply();
    }

    @NonNull
    private String getKey(String key) {
        return prefix  + "_" + key;
    }
}
