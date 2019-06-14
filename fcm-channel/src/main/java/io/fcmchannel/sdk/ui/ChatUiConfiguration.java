package io.fcmchannel.sdk.ui;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;

import io.fcmchannel.sdk.FcmClient;

import static io.fcmchannel.sdk.ui.UiConfiguration.INVALID_VALUE;

public class ChatUiConfiguration {

    @DrawableRes
    private int receivedMessageIconRes = INVALID_VALUE;
    @DrawableRes
    private int sentMessageBackgroundRes = INVALID_VALUE;
    @DrawableRes
    private int receivedMessageBackgroundRes = INVALID_VALUE;
    @ColorInt
    private int sentMessageBackgroundColor = INVALID_VALUE;
    @ColorInt
    private int receivedMessageBackgroundColor = INVALID_VALUE;
    @ColorInt
    private int sentMessageTextColor = INVALID_VALUE;
    @ColorInt
    private int receivedMessageTextColor = INVALID_VALUE;

    public int getReceivedMessageIconRes() {
        return receivedMessageIconRes != INVALID_VALUE ? receivedMessageIconRes : FcmClient.getAppIcon();
    }

    public ChatUiConfiguration setReceivedMessageIconRes(int receivedMessageIconRes) {
        this.receivedMessageIconRes = receivedMessageIconRes;
        return this;
    }

    @DrawableRes
    public int getSentMessageBackgroundRes() {
        return sentMessageBackgroundRes;
    }

    public ChatUiConfiguration setSentMessageBackgroundRes(@DrawableRes int sentMessageBackgroundRes) {
        this.sentMessageBackgroundRes = sentMessageBackgroundRes;
        return this;
    }

    @DrawableRes
    public int getReceivedMessageBackgroundRes() {
        return receivedMessageBackgroundRes;
    }

    public ChatUiConfiguration setReceivedMessageBackgroundRes(@DrawableRes int receivedMessageBackgroundRes) {
        this.receivedMessageBackgroundRes = receivedMessageBackgroundRes;
        return this;
    }

    public int getSentMessageBackgroundColor() {
        return sentMessageBackgroundColor;
    }

    public ChatUiConfiguration setSentMessageBackgroundColor(int sentMessageBackgroundColor) {
        this.sentMessageBackgroundColor = sentMessageBackgroundColor;
        return this;
    }

    public int getReceivedMessageBackgroundColor() {
        return receivedMessageBackgroundColor;
    }

    public ChatUiConfiguration setReceivedMessageBackgroundColor(int receivedMessageBackgroundColor) {
        this.receivedMessageBackgroundColor = receivedMessageBackgroundColor;
        return this;
    }

    public int getSentMessageTextColor() {
        return sentMessageTextColor;
    }

    public ChatUiConfiguration setSentMessageTextColor(int sentMessageTextColor) {
        this.sentMessageTextColor = sentMessageTextColor;
        return this;
    }

    public int getReceivedMessageTextColor() {
        return receivedMessageTextColor;
    }

    public ChatUiConfiguration setReceivedMessageTextColor(int receivedMessageTextColor) {
        this.receivedMessageTextColor = receivedMessageTextColor;
        return this;
    }

}
