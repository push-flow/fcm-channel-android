package io.fcmchannel.sdk.chat;

import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import io.fcmchannel.sdk.R;
import io.fcmchannel.sdk.ui.ChatUiConfiguration;

import static io.fcmchannel.sdk.ui.UiConfiguration.INVALID_VALUE;

class LoadingViewHolder extends RecyclerView.ViewHolder {

    LoadingViewHolder(ViewGroup parent, ChatUiConfiguration chatUiConfiguration) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.fcm_client_item_loading, parent, false));
        setupProgressBar(chatUiConfiguration);
    }

    private void setupProgressBar(ChatUiConfiguration uiConfiguration) {
        int loadingColor = uiConfiguration.getMessagesLoadingColor();
        if (loadingColor == INVALID_VALUE) return;

        ColorFilter colorFilter = new PorterDuffColorFilter(loadingColor, PorterDuff.Mode.SRC_IN);
        ProgressBar progressBar = itemView.findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(colorFilter);
    }

}
