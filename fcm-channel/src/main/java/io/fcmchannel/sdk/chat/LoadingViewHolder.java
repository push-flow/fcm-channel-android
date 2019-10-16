package io.fcmchannel.sdk.chat;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import io.fcmchannel.sdk.R;

class LoadingViewHolder extends RecyclerView.ViewHolder {

    LoadingViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.fcm_client_item_loading, parent, false));
    }

}
