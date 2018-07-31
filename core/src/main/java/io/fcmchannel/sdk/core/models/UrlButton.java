package io.fcmchannel.sdk.core.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UrlButton implements Parcelable {

    private String title;
    private String url;

    protected UrlButton(Parcel in) {
        title = in.readString();
        url = in.readString();
    }

    public static final Creator<UrlButton> CREATOR = new Creator<UrlButton>() {
        @Override
        public UrlButton createFromParcel(Parcel in) {
            return new UrlButton(in);
        }

        @Override
        public UrlButton[] newArray(int size) {
            return new UrlButton[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(url);
    }
}
