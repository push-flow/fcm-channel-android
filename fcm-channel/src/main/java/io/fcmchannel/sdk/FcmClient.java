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
import android.text.TextUtils;

import androidx.annotation.DrawableRes;

import java.util.Collections;

import io.fcmchannel.sdk.chat.FcmClientChatActivity;
import io.fcmchannel.sdk.chat.FcmClientChatFragment;
import io.fcmchannel.sdk.chat.menu.FcmClientMenuService;
import io.fcmchannel.sdk.core.models.Contact;
import io.fcmchannel.sdk.core.models.network.FcmRegistrationResponse;
import io.fcmchannel.sdk.core.network.RestServices;
import io.fcmchannel.sdk.listeners.SendMessageListener;
import io.fcmchannel.sdk.permission.PermissionDialog;
import io.fcmchannel.sdk.persistence.Preferences;
import io.fcmchannel.sdk.services.FcmClientRegistrationIntentService;
import io.fcmchannel.sdk.ui.UiConfiguration;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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

    private static Preferences preferences;
    private static RestServices services;

    public static final String URN_PREFIX_FCM = "fcm:";

    FcmClient() {}

    public static void initialize(final Builder builder) {
        initialize(
            builder.context,
            builder.host,
            builder.token,
            builder.channel,
            builder.registrationServiceClass,
            builder.uiConfiguration
        );
    }

    private static void initialize(
        final Context context,
        final String host,
        final String token,
        final String channel,
        final Class<? extends FcmClientRegistrationIntentService> registrationServiceClass,
        final UiConfiguration uiConfiguration
    ) {
        if (context == null) {
            throw new IllegalArgumentException("It's not possible to initialize FcmClient without a context");
        }
        FcmClient.context = context;
        FcmClient.host = host;
        FcmClient.token = token;
        FcmClient.channel = channel;
        FcmClient.registrationServiceClass = registrationServiceClass;
        FcmClient.preferences = new Preferences(context);

        if (!TextUtils.isEmpty(host) && !TextUtils.isEmpty(getToken())) {
            FcmClient.services = new RestServices(host, getToken());
        }
        FcmClient.uiConfiguration = uiConfiguration;
        FcmClient.getPreferences()
            .setFloatingChatEnabled(uiConfiguration.isFloatingChatEnabled())
            .commit();
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

    public static void setUiConfiguration(final UiConfiguration uiConfiguration) {
        FcmClient.uiConfiguration = uiConfiguration;
    }

    public static UiConfiguration getUiConfiguration() {
        return uiConfiguration;
    }

    public static void setPreferences(final Preferences preferences) {
        FcmClient.preferences = preferences;
    }

    public static Preferences getPreferences() {
        if (preferences == null) {
            preferences = new Preferences(context);
        }
        return preferences;
    }

    public static RestServices getServices() {
        return services;
    }

    public static void startFcmClientChatActivity(final Context context) {
        startFcmClientChatActivity(context, token, channel);
    }

    public static FcmClientChatFragment createFcmClientChatFragment() {
        return new FcmClientChatFragment();
    }

    public static FcmClientChatFragment getFcmClientChatFragment(
        final String token,
        final String channel
    ) {
        FcmClient.token = token;
        FcmClient.channel = channel;
        FcmClient.services = new RestServices(host, token);

        return new FcmClientChatFragment();
    }

    public static void startFcmClientChatActivity(
        final Context context,
        final String token,
        final String channel
    ) {
        FcmClient.token = token;
        FcmClient.channel = channel;
        FcmClient.services = new RestServices(host, token);
        context.startActivity(new Intent(context, FcmClientChatActivity.class));
    }

    public static void sendMessage(final String message) {
        services.sendReceivedMessage(
            channel,
            getPreferences().getUrn(),
            getPreferences().getFcmToken(),
            message
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                new Action() {
                    @Override
                    public void run() { }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) { }
                }
            );
    }

    public static void sendMessage(final String message, final SendMessageListener listener) {
        services.sendReceivedMessage(
            channel,
            getPreferences().getUrn(),
            getPreferences().getFcmToken(),
            message
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                new Action() {
                    @Override
                    public void run() {
                        listener.onSendMessage();
                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        final String message = context.getString(R.string.fcm_client_error_message_send);
                        listener.onError(throwable, message);
                    }
                }
            );
    }

    public static Contact getContact() {
        final Contact contact = new Contact();
        contact.setUuid(getPreferences().getContactUuid());
        contact.setUrns(Collections.singletonList(getPreferences().getUrn()));
        return contact;
    }

    public static Single<FcmRegistrationResponse> saveContactWithToken(
        final String urn,
        final String fcmToken,
        final String contactUuid,
        final String token
    ) {
        final RestServices restServices = new RestServices(host, token);
        return restServices.registerFcmContact(channel, urn, fcmToken, contactUuid);
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
        getPreferences().clear();
    }

    public static void registerContactIfNeeded(final String urn) {
        if (!isContactRegistered()) {
            registerContact(urn);
        }
    }

    public static void registerContact(final String urn) {
        registerContact(urn, null);
    }

    public static void registerContact(final String urn, final String contactUuid) {
        final Class<? extends FcmClientRegistrationIntentService> registrationIntentService =
                registrationServiceClass != null ? registrationServiceClass : FcmClientRegistrationIntentService.class;

        final Intent registrationIntent = new Intent(context, registrationIntentService);
        registrationIntent.putExtra(FcmClientRegistrationIntentService.EXTRA_URN, urn);

        if (!TextUtils.isEmpty(contactUuid)) {
            registrationIntent.putExtra(FcmClientRegistrationIntentService.EXTRA_CONTACT_UUID, contactUuid);
        }
        context.startService(registrationIntent);
    }

    public static boolean isContactRegistered() {
        return !TextUtils.isEmpty(getPreferences().getFcmToken())
            && !TextUtils.isEmpty(getPreferences().getContactUuid());
    }

    public static boolean isChatVisible() {
        return FcmClientChatFragment.visible || FcmClientMenuService.isExpanded();
    }

    @DrawableRes
    public static int getAppIcon() {
        try {
            final PackageManager packageManager = context.getPackageManager();
            final ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

            return info.icon;
        } catch (Exception exception) {
            return R.drawable.fcm_client_ic_send_message;
        }
    }

    public static void requestFloatingPermissionsIfNeeded(final Activity activity) {
        if (!hasFloatingPermission()) {
            requestFloatingPermissions(activity);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void requestFloatingPermissions(final Activity activity) {
        final PermissionDialog permissionDialog = new PermissionDialog(activity);
        permissionDialog.show();
    }

    public static void showManageOverlaySettings() {
        final Intent drawOverlaysSettingsIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        drawOverlaysSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        drawOverlaysSettingsIntent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(drawOverlaysSettingsIntent);
    }

    public static boolean hasFloatingPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
    }

    public static class Builder {

        private Context context;
        private String token;
        private String host;
        private String channel;
        private Class<? extends FcmClientRegistrationIntentService> registrationServiceClass
            = FcmClientRegistrationIntentService.class;
        private UiConfiguration uiConfiguration;

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

        public Builder setRegistrationServiceClass(
            Class<? extends FcmClientRegistrationIntentService> registrationServiceClass
        ) {
            this.registrationServiceClass = registrationServiceClass;
            return this;
        }

        public Builder setUiConfiguration(UiConfiguration uiConfiguration) {
            this.uiConfiguration = uiConfiguration;
            return this;
        }
    }

}
