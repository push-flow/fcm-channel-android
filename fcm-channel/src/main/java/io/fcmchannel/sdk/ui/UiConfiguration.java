package io.fcmchannel.sdk.ui;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.StyleRes;

import io.fcmchannel.sdk.FcmClient;
import io.fcmchannel.sdk.R;

/**
 * Created by john-mac on 7/1/16.
 */
public class UiConfiguration {

    public static final int INVALID_VALUE = 0;

    @DrawableRes
    private int backResource = R.drawable.fcm_client_ic_arrow_back_white;
    @DrawableRes
    private int floatingChatIcon = INVALID_VALUE;
    @ColorInt
    private int toolbarColor = INVALID_VALUE;
    @ColorInt
    private int titleColor = INVALID_VALUE;
    @StyleRes
    private int theme = INVALID_VALUE;

    private ChatUiConfiguration chatUiConfiguration = new ChatUiConfiguration();
    private String permissionMessage = "";
    private String titleString = "";
    private boolean floatingChatEnabled = true;

    public int getBackResource() {
        return backResource;
    }

    public UiConfiguration setBackResource(int backResource) {
        this.backResource = backResource;
        return this;
    }

    public int getToolbarColor() {
        return toolbarColor;
    }

    public UiConfiguration setToolbarColor(@ColorInt int toolbarColor) {
        this.toolbarColor = toolbarColor;
        return this;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public UiConfiguration setTitleColor(@ColorInt int titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    public int getTheme() {
        return theme;
    }

    public UiConfiguration setTheme(@StyleRes int theme) {
        this.theme = theme;
        return this;
    }

    public ChatUiConfiguration getChatUiConfiguration() {
        return chatUiConfiguration;
    }

    public UiConfiguration setChatUiConfiguration(ChatUiConfiguration chatUiConfiguration) {
        this.chatUiConfiguration = chatUiConfiguration;
        return this;
    }

    public String getTitleString() {
        return titleString;
    }

    public UiConfiguration setTitleString(String titleString) {
        this.titleString = titleString;
        return this;
    }

    public boolean isFloatingChatEnabled() {
        return floatingChatEnabled;
    }

    public UiConfiguration setFloatingChatEnabled(boolean enabled) {
        floatingChatEnabled = enabled;
        return this;
    }

    @DrawableRes
    public int getFloatingChatIcon() {
        return floatingChatIcon != UiConfiguration.INVALID_VALUE ? floatingChatIcon : FcmClient.getAppIcon();
    }

    public UiConfiguration setFloatingChatIcon(@DrawableRes int floatingChatIcon) {
        this.floatingChatIcon = floatingChatIcon;
        return this;
    }

    public String getPermissionMessage() {
        return permissionMessage;
    }

    public UiConfiguration setPermissionMessage(String permissionMessage) {
        this.permissionMessage = permissionMessage;
        return this;
    }

}
