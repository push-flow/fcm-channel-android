package io.fcmchannel.sdk.core.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessageMetadata {

    public MessageMetadata() {}

    @SerializedName("url_buttons")
    private List<UrlButton> urlButtons;
    @SerializedName("quick_replies")
    private List<QuickReply> quickReplies;

    public List<UrlButton> getUrlButtons() {
        return urlButtons;
    }

    public void setUrlButtons(List<UrlButton> urlButtons) {
        this.urlButtons = urlButtons;
    }

    public List<QuickReply> getQuickReplies() {
        return quickReplies;
    }

    public void setQuickReply(List<QuickReply> quickReplies) {
        this.quickReplies = quickReplies;
    }
}
