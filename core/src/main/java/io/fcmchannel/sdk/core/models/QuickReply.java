package io.fcmchannel.sdk.core.models;

import android.os.Parcel;
import android.os.Parcelable;

public class QuickReply implements Parcelable {

    private String title;

    protected QuickReply(Parcel in) {
        title = in.readString();
    }

    public static final Creator<QuickReply> CREATOR = new Creator<QuickReply>() {
        @Override
        public QuickReply createFromParcel(Parcel in) {
            return new QuickReply(in);
        }

        @Override
        public QuickReply[] newArray(int size) {
            return new QuickReply[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
    }
}
