package io.fcmchannel.sdk.core.models;

import com.google.gson.annotations.SerializedName;

public class Attachment {

    @SerializedName("content_type")
    private String contentType;
    private String url;

    public String getContentType() {
        return contentType;
    }

    public Attachment setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Attachment setUrl(String url) {
        this.url = url;
        return this;
    }

}
