package io.fcmchannel.sdk.chat.viewholder;

import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

import io.fcmchannel.sdk.R;
import io.fcmchannel.sdk.core.models.Message;
import io.fcmchannel.sdk.databinding.FcmClientItemLoadingBinding;
import io.fcmchannel.sdk.ui.ChatUiConfiguration;

public class LoadingViewHolder extends ChatViewHolder {

    public LoadingViewHolder(FcmClientItemLoadingBinding binding, ChatUiConfiguration uiConfig) {
        super(binding.getRoot());
        setupProgressBar(uiConfig);
    }

    @Override
    protected void onBind(Message nullObject) { }

    private void setupProgressBar(ChatUiConfiguration uiConfig) {
        int color = ContextCompat.getColor(itemView.getContext(), uiConfig.getMessageLoadingColor());
        ColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        ProgressBar progressBar = itemView.findViewById(R.id.progressBar);

        progressBar.getIndeterminateDrawable().setColorFilter(colorFilter);
    }

}
