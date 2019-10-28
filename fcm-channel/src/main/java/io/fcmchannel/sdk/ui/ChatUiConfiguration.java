package io.fcmchannel.sdk.ui;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

import java.text.DateFormat;

import io.fcmchannel.sdk.FcmClient;
import io.fcmchannel.sdk.R;

public class ChatUiConfiguration {

    @ColorRes
    private int sentMessageBackgroundColor = R.color.fcm_client_rice_flower;
    @ColorRes
    private int sentMessageTextColor = R.color.fcm_client_black;
    @ColorRes
    private int sentMessageHourTextColor = R.color.fcm_client_chelsea_cucumber;
    @ColorRes
    private int receivedMessageBackgroundColor = R.color.fcm_client_white;
    @ColorRes
    private int receivedMessageTextColor = R.color.fcm_client_black;
    @ColorRes
    private int receivedMessageHourTextColor = R.color.fcm_client_gray_chateau;
    @DrawableRes
    private int receivedMessageAvatar = FcmClient.getAppIcon();

    private DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
    private boolean sentMessageTopIndicator = false;
    private boolean receivedMessageTopIndicator = false;

    @ColorRes
    private int metadataBackgroundColor = R.color.fcm_client_white;
    @ColorRes
    private int metadataTextColor = R.color.fcm_client_black;

    @ColorRes
    private int sendMessageButtonColor = R.color.fcm_client_picton_blue;

    @DrawableRes
    private int chatBackground = R.drawable.fcm_client_bg_chat;
    @DrawableRes
    private int chatBackgroundImage = 0;
    @ColorRes
    private int messageLoadingColor = R.color.fcm_client_gray_light_2;

    private int messagePageSize = 0;

    @ColorRes
    public int getSentMessageBackgroundColor() {
        return sentMessageBackgroundColor;
    }

    public ChatUiConfiguration setSentMessageBackgroundColor(@ColorRes int sentMessageBackgroundColor) {
        this.sentMessageBackgroundColor = sentMessageBackgroundColor;
        return this;
    }

    @ColorRes
    public int getReceivedMessageBackgroundColor() {
        return receivedMessageBackgroundColor;
    }

    public ChatUiConfiguration setReceivedMessageBackgroundColor(@ColorRes int receivedMessageBackgroundColor) {
        this.receivedMessageBackgroundColor = receivedMessageBackgroundColor;
        return this;
    }

    @ColorRes
    public int getSentMessageTextColor() {
        return sentMessageTextColor;
    }

    public ChatUiConfiguration setSentMessageTextColor(@ColorRes int sentMessageTextColor) {
        this.sentMessageTextColor = sentMessageTextColor;
        return this;
    }

    @ColorRes
    public int getSentMessageHourTextColor() {
        return sentMessageHourTextColor;
    }

    public ChatUiConfiguration setSentMessageHourTextColor(@ColorRes int sentMessageHourTextColor) {
        this.sentMessageHourTextColor = sentMessageHourTextColor;
        return this;
    }

    @ColorRes
    public int getReceivedMessageTextColor() {
        return receivedMessageTextColor;
    }

    public ChatUiConfiguration setReceivedMessageTextColor(@ColorRes int receivedMessageTextColor) {
        this.receivedMessageTextColor = receivedMessageTextColor;
        return this;
    }

    @ColorRes
    public int getReceivedMessageHourTextColor() {
        return receivedMessageHourTextColor;
    }

    public ChatUiConfiguration setReceivedMessageHourTextColor(@ColorRes int receivedMessageHourTextColor) {
        this.receivedMessageHourTextColor = receivedMessageHourTextColor;
        return this;
    }

    @DrawableRes
    public int getReceivedMessageAvatar() {
        return receivedMessageAvatar;
    }

    public ChatUiConfiguration setReceivedMessageAvatar(@DrawableRes int receivedMessageAvatar) {
        this.receivedMessageAvatar = receivedMessageAvatar;
        return this;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public ChatUiConfiguration setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    public boolean isSentMessageTopIndicator() {
        return sentMessageTopIndicator;
    }

    public ChatUiConfiguration setSentMessageTopIndicator(boolean sentMessageTopIndicator) {
        this.sentMessageTopIndicator = sentMessageTopIndicator;
        return this;
    }

    public boolean isReceivedMessageTopIndicator() {
        return receivedMessageTopIndicator;
    }

    public ChatUiConfiguration setReceivedMessageTopIndicator(boolean receivedMessageTopIndicator) {
        this.receivedMessageTopIndicator = receivedMessageTopIndicator;
        return this;
    }

    @ColorRes
    public int getMetadataBackgroundColor() {
        return metadataBackgroundColor;
    }

    public ChatUiConfiguration setMetadataBackgroundColor(@ColorRes int metadataBackgroundColor) {
        this.metadataBackgroundColor = metadataBackgroundColor;
        return this;
    }

    @ColorRes
    public int getMetadataTextColor() {
        return metadataTextColor;
    }

    public ChatUiConfiguration setMetadataTextColor(@ColorRes int metadataTextColor) {
        this.metadataTextColor = metadataTextColor;
        return this;
    }

    @ColorRes
    public int getSendMessageButtonColor() {
        return sendMessageButtonColor;
    }

    public ChatUiConfiguration setSendMessageButtonColor(@ColorRes int sendMessageButtonColor) {
        this.sendMessageButtonColor = sendMessageButtonColor;
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

    @ColorRes
    public int getMessageLoadingColor() {
        return messageLoadingColor;
    }

    public ChatUiConfiguration setMessageLoadingColor(@ColorRes int messageLoadingColor) {
        this.messageLoadingColor = messageLoadingColor;
        return this;
    }

    public int getMessagePageSize() {
        return messagePageSize;
    }

    public ChatUiConfiguration setMessagePageSize(int messagePageSize) {
        this.messagePageSize = messagePageSize;
        return this;
    }

    public boolean messagePagingEnabled() {
        return messagePageSize > 0;
    }

}
