package io.fcmchannel.sdk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Collections;
import java.util.UUID;

import io.fcmchannel.sdk.chat.FcmClientChatActivity;
import io.fcmchannel.sdk.chat.FcmClientChatFragment;
import io.fcmchannel.sdk.chat.menu.FcmClientMenuService;
import io.fcmchannel.sdk.core.models.network.FcmRegistrationResponse;
import io.fcmchannel.sdk.core.models.Contact;
import io.fcmchannel.sdk.core.network.RestServices;
import io.fcmchannel.sdk.listeners.SendMessageListener;
import io.fcmchannel.sdk.permission.PermissionDialog;
import io.fcmchannel.sdk.persistence.Preferences;
import io.fcmchannel.sdk.services.FcmClientRegistrationIntentService;
import io.fcmchannel.sdk.ui.UiConfiguration;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by John Cordeiro on 5/10/17.
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class FcmClient {

    private static Context context;
    private static String token;
    private static String host;
    private static String channel;
    private static Boolean forceRegistration = false;
    private static Class<? extends FcmClientRegistrationIntentService> registrationServiceClass;
    private static UiConfiguration uiConfiguration;
    private static boolean safeModeEnabled = false;

    private static Preferences preferences;
    private static RestServices services;

    public static final String URN_PREFIX_FCM = "fcm:";

    FcmClient() {}

    public static void initialize(Builder builder) {
        initialize(
            builder.context,
            builder.host,
            builder.token,
            builder.channel,
            builder.registrationServiceClass,
            builder.uiConfiguration,
            builder.safeModeEnabled
        );
    }

    private static void initialize(
        Context context,
        String host,
        String token,
        String channel,
        Class<? extends FcmClientRegistrationIntentService> registrationServiceClass,
        UiConfiguration uiConfiguration,
        boolean safeModeEnabled
    ) {
        if (context == null) {
            throw new IllegalArgumentException("It's not possible to initialize FcmClient without a context");
        }
        if (safeModeEnabled && token != null) {
            throw new IllegalArgumentException("Token must not be set on safe mode");
        }
        FcmClient.context = context;
        FcmClient.host = host;
        FcmClient.token = token;
        FcmClient.channel = channel;
        FcmClient.registrationServiceClass = registrationServiceClass;
        FcmClient.preferences = new Preferences(context);
        FcmClient.uiConfiguration = uiConfiguration;
        FcmClient.safeModeEnabled = safeModeEnabled;

        if (!TextUtils.isEmpty(host)) {
            FcmClient.services = new RestServices(host, getToken());
        }
        FcmClient.getPreferences()
                .setFloatingChatEnabled(uiConfiguration.isFloatingChatEnabled())
                .commit();
    }

    public static void startFcmClientChatActivity(Context context) {
        startFcmClientChatActivity(context, token, channel);
    }

    public static FcmClientChatFragment createFcmClientChatFragment() {
        return new FcmClientChatFragment();
    }

    public static FcmClientChatFragment getFcmClientChatFragment(String token, String channel) {
        FcmClient.token = token;
        FcmClient.channel = channel;
        FcmClient.services = new RestServices(host, token);
        return new FcmClientChatFragment();
    }

    public static void startFcmClientChatActivity(Context context, String token, String channel) {
        FcmClient.token = token;
        FcmClient.channel = channel;
        FcmClient.services = new RestServices(host, token);
        context.startActivity(new Intent(context, FcmClientChatActivity.class));
    }

    public static void setUiConfiguration(UiConfiguration uiConfiguration) {
        FcmClient.uiConfiguration = uiConfiguration;
    }

    public static void sendMessage(String message) {
        services.sendReceivedMessage(channel, getPreferences().getUrn(), getFcmToken(), message)
                .enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {}
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
    }

    public static void sendMessage(String message, final SendMessageListener listener) {
        services.sendReceivedMessage(channel, getPreferences().getUrn(), getFcmToken(), message)
                .enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    listener.onSendMessage();
                } else {
                    listener.onError(getExceptionForErrorResponse(response), context.getString(R.string.fcm_client_error_message_send));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                listener.onError(throwable, context.getString(R.string.fcm_client_error_message_send));
            }
        });
    }

    public static Contact getContact() {
        Contact contact = new Contact();
        contact.setUuid(getPreferences().getContactUuid());
        contact.setUrns(Collections.singletonList(getPreferences().getUrn()));
        return contact;
    }

    public static Response<FcmRegistrationResponse> saveContactWithToken(String urn, String fcmToken,
                                                                         String contactUuid, String token) throws java.io.IOException {
        RestServices restServices = new RestServices(host, token);
        return restServices.registerFcmContact(channel, urn, fcmToken, contactUuid).execute();
    }

    @NonNull
    private static IllegalStateException getExceptionForErrorResponse(Response response) {
        return new IllegalStateException("Response not successful. HTTP Code: " + response.code() + " Response: " + response.raw());
    }

    public static int getUnreadMessages() {
        return getPreferences().getUnreadMessages();
    }

    public static void refreshContactToken() {
        if (isContactRegistered()) {
            registerContact(getPreferences().getUrn(), getPreferences().getContactUuid());
        }
    }

    public static void clearContact() {
        Preferences preferences = getPreferences();
        preferences.clear();
    }

    public static void registerContactIfNeeded(String urn) {
        if (!isContactRegistered()) {
            registerContact(urn);
        }
    }

    public static void registerContact(String urn) {
        registerContact(urn, null);
    }

    public static void registerContact(String urn, String contactUuid) {
        Class<? extends FcmClientRegistrationIntentService> registrationIntentService =
                registrationServiceClass != null ? registrationServiceClass : FcmClientRegistrationIntentService.class;

        Intent registrationIntent = new Intent(context, registrationIntentService);
        registrationIntent.putExtra(FcmClientRegistrationIntentService.EXTRA_URN, urn);
        if (!TextUtils.isEmpty(contactUuid)) {
            registrationIntent.putExtra(FcmClientRegistrationIntentService.EXTRA_CONTACT_UUID, contactUuid);
        }
        context.startService(registrationIntent);
    }

    public static boolean isContactRegistered() {
        return !TextUtils.isEmpty(getPreferences().getUrn());
    }

    public static RestServices getServices() {
        return services;
    }

    public static boolean isChatVisible() {
        return FcmClientChatFragment.visible || FcmClientMenuService.isExpanded();
    }

    public static void setPreferences(Preferences preferences) {
        FcmClient.preferences = preferences;
    }

    public static Preferences getPreferences() {
        if (preferences == null) {
            preferences = new Preferences(context);
        }
        return preferences;
    }

    @DrawableRes
    public static int getAppIcon() {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return info.icon;
        } catch (Exception exception) {
            return R.drawable.fcm_client_ic_send_message;
        }
    }

    public static void requestFloatingPermissionsIfNeeded(Activity activity) {
        if (!hasFloatingPermission()) {
            requestFloatingPermissions(activity);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void requestFloatingPermissions(Activity activity) {
        PermissionDialog permissionDialog = new PermissionDialog(activity);
        permissionDialog.show();
    }

    public static void showManageOverlaySettings() {
        Intent drawOverlaysSettingsIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        drawOverlaysSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        drawOverlaysSettingsIntent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(drawOverlaysSettingsIntent);
    }

    public static boolean hasFloatingPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
            || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(context));
    }

    public static String getFcmToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }

    public static Context getContext() {
        return context;
    }

    public static String getToken() {
        return token;
    }

    public static String getChannel() {
        return channel;
    }

    public static String getHost() {
        return host;
    }

    public static UiConfiguration getUiConfiguration() {
        return uiConfiguration;
    }

    public static boolean isSafeModeEnabled() {
        return safeModeEnabled;
    }

    public static class Builder {

        private Context context;
        private String token;
        private String host;
        private String channel;
        private Class<? extends FcmClientRegistrationIntentService> registrationServiceClass = FcmClientRegistrationIntentService.class;
        private UiConfiguration uiConfiguration;
        private boolean safeModeEnabled;

        public Builder(Context context) {
            this.context = context;
            this.uiConfiguration = new UiConfiguration();
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setChannel(String channel) {
            this.channel = channel;
            return this;
        }

        public Builder setRegistrationServiceClass(Class<? extends FcmClientRegistrationIntentService>
                                                           registrationServiceClass) {
            this.registrationServiceClass = registrationServiceClass;
            return this;
        }

        public Builder setUiConfiguration(UiConfiguration uiConfiguration) {
            this.uiConfiguration = uiConfiguration;
            return this;
        }

        public boolean isSafeModeEnabled() {
            return safeModeEnabled;
        }

        public Builder setSafeModeEnabled(boolean enabled) {
            this.safeModeEnabled = enabled;
            return this;
        }
    }

}
