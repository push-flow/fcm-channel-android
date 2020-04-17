package io.fcmchannel.sdk.ui;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

import io.fcmchannel.sdk.FcmClient;

import static io.fcmchannel.sdk.ui.UiConfiguration.INVALID_VALUE;

public class ChatUiConfiguration {

    @DrawableRes
    private int sentMessageBackground = INVALID_VALUE;
    @DrawableRes
    private int receivedMessageBackground = INVALID_VALUE;
    @ColorInt
    private int sentMessageBackgroundColor = INVALID_VALUE;
    @ColorInt
    private int receivedMessageBackgroundColor = INVALID_VALUE;
    @ColorInt
    private int sentMessageTextColor = INVALID_VALUE;
    @ColorInt
    private int receivedMessageTextColor = INVALID_VALUE;
    @ColorInt
    private int sendMessageIconColor = INVALID_VALUE;
    @ColorInt
    private int receivedMessageIcon = INVALID_VALUE;

    private boolean sentMessageInTopDirection = false;
    private boolean receivedMessageInTopDirection = false;
    private boolean showMediaLink = false;

    @ColorInt
    private int sentMessageHourTextColor = INVALID_VALUE;
    @ColorInt
    private int receivedMessageHourTextColor = INVALID_VALUE;
    @DrawableRes
    private int metadataBackground = INVALID_VALUE;
    @DrawableRes
    private int metadataBackgroundColor = INVALID_VALUE;
    @DrawableRes
    private int chatBackground = INVALID_VALUE;
    @DrawableRes
    private int chatBackgroundImage = INVALID_VALUE;

    private int messagesPageSize = -1;
    @ColorInt
    private int messagesLoadingColor = INVALID_VALUE;

    private String initialPayload;

    @DrawableRes
    public int getSentMessageBackground() {
        return sentMessageBackground;
    }

    public ChatUiConfiguration setSentMessageBackground(@DrawableRes int sentMessageBackground) {
        this.sentMessageBackground = sentMessageBackground;
        return this;
    }

    @DrawableRes
    public int getReceivedMessageBackground() {
        return receivedMessageBackground;
    }

    public ChatUiConfiguration setReceivedMessageBackground(@DrawableRes int receivedMessageBackground) {
        this.receivedMessageBackground = receivedMessageBackground;
        return this;
    }

    @ColorInt
    public int getSentMessageBackgroundColor() {
        return sentMessageBackgroundColor;
    }

    public ChatUiConfiguration setSentMessageBackgroundColor(@ColorInt int sentMessageBackgroundColor) {
        this.sentMessageBackgroundColor = sentMessageBackgroundColor;
        return this;
    }

    @ColorInt
    public int getReceivedMessageBackgroundColor() {
        return receivedMessageBackgroundColor;
    }

    public ChatUiConfiguration setReceivedMessageBackgroundColor(@ColorInt int receivedMessageBackgroundColor) {
        this.receivedMessageBackgroundColor = receivedMessageBackgroundColor;
        return this;
    }

    @ColorInt
    public int getSentMessageTextColor() {
        return sentMessageTextColor;
    }

    public ChatUiConfiguration setSentMessageTextColor(@ColorInt int sentMessageTextColor) {
        this.sentMessageTextColor = sentMessageTextColor;
        return this;
    }

    @ColorInt
    public int getReceivedMessageTextColor() {
        return receivedMessageTextColor;
    }

    public ChatUiConfiguration setReceivedMessageTextColor(@ColorInt int receivedMessageTextColor) {
        this.receivedMessageTextColor = receivedMessageTextColor;
        return this;
    }

    @ColorInt
    public int getSendMessageIconColor() {
        return sendMessageIconColor;
    }

    public ChatUiConfiguration setSendMessageIconColor(@ColorInt int sendMessageIconColor) {
        this.sendMessageIconColor = sendMessageIconColor;
        return this;
    }

    @DrawableRes
    public int getReceivedMessageIcon() {
        return receivedMessageIcon != INVALID_VALUE ? receivedMessageIcon : FcmClient.getAppIcon();
    }

    public ChatUiConfiguration setReceivedMessageIcon(@DrawableRes int receivedMessageIcon) {
        this.receivedMessageIcon = receivedMessageIcon;
        return this;
    }

    public boolean isShowMediaLink() {
        return showMediaLink;
    }

    public ChatUiConfiguration setShowMediaLink(boolean showMediaLink) {
        this.showMediaLink = showMediaLink;
        return this;
    }

    public boolean isReceivedMessageInTopDirection() {
        return receivedMessageInTopDirection;
    }

    public ChatUiConfiguration setReceivedMessageInTopDirection(boolean receivedMessageInTopDirection) {
        this.receivedMessageInTopDirection = receivedMessageInTopDirection;
        return this;
    }

    public boolean isSentMessageInTopDirection() {
        return sentMessageInTopDirection;
    }

    public ChatUiConfiguration setSentMessageInTopDirection(boolean sentMessageInTopDirection) {
        this.sentMessageInTopDirection = sentMessageInTopDirection;
        return this;
    }

    @ColorInt
    public int getSentMessageHourTextColor() {
        return sentMessageHourTextColor;
    }

    public ChatUiConfiguration setSentMessageHourTextColor(@ColorInt int sentMessageHourTextColor) {
        this.sentMessageHourTextColor = sentMessageHourTextColor;
        return this;
    }

    @ColorInt
    public int getReceivedMessageHourTextColor() {
        return receivedMessageHourTextColor;
    }

    public ChatUiConfiguration setReceivedMessageHourTextColor(@ColorInt int receivedMessageHourTextColor) {
        this.receivedMessageHourTextColor = receivedMessageHourTextColor;
        return this;
    }

    @DrawableRes
    public int getMetadataBackground() {
        return metadataBackground;
    }

    public ChatUiConfiguration setMetadataBackground(@DrawableRes int metadataBackground) {
        this.metadataBackground = metadataBackground;
        return this;
    }

    @ColorInt
    public int getMetadataBackgroundColor() {
        return metadataBackgroundColor;
    }

    public ChatUiConfiguration setMetadataBackgroundColor(@ColorInt int metadataBackgroundColor) {
        this.metadataBackgroundColor = metadataBackgroundColor;
        return this;
    }

    @DrawableRes
    public int getChatBackground() {
        return chatBackground;
    }

    public ChatUiConfiguration setChatBackground(@DrawableRes int chatBackground) {
        this.chatBackground = chatBackground;
        return this;
    }

    @DrawableRes
    public int getChatBackgroundImage() {
        return chatBackgroundImage;
    }

    public ChatUiConfiguration setChatBackgroundImage(@DrawableRes int chatBackgroundImage) {
        this.chatBackgroundImage = chatBackgroundImage;
        return this;
    }

    public int getMessagesPageSize() {
        return messagesPageSize;
    }

    public ChatUiConfiguration setMessagesPageSize(int messagesPageSize) {
        this.messagesPageSize = messagesPageSize;
        return this;
    }

    public boolean messagesPagingEnabled() {
        return getMessagesPageSize() > 0;
    }

    @ColorInt
    public int getMessagesLoadingColor() {
        return messagesLoadingColor;
    }

    public ChatUiConfiguration setMessagesLoadingColor(@ColorInt int messagesLoadingColor) {
        this.messagesLoadingColor = messagesLoadingColor;
        return this;
    }

    public String getInitialPayload() {
        return initialPayload;
    }

    public ChatUiConfiguration setInitialPayload(String initialPayload) {
        this.initialPayload = initialPayload;
        return this;
    }
}
