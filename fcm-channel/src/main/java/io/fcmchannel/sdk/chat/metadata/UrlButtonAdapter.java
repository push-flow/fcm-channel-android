package io.fcmchannel.sdk.chat.metadata;

import android.graphics.PorterDuff;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.fcmchannel.sdk.R;
import io.fcmchannel.sdk.core.models.UrlButton;
import io.fcmchannel.sdk.ui.ChatUiConfiguration;

import static io.fcmchannel.sdk.ui.UiConfiguration.INVALID_VALUE;

public class UrlButtonAdapter extends RecyclerView.Adapter<UrlButtonAdapter.ViewHolder> {

    private ChatUiConfiguration chatUiConfiguration;
    private List<UrlButton> urlButtons;
    private OnMetadataItemClickListener onMetadataItemClickListener;

    public UrlButtonAdapter(
            ChatUiConfiguration chatUiConfiguration,
            List<UrlButton> urlButtons,
            OnMetadataItemClickListener onMetadataItemClickListener
    ) {
        this.chatUiConfiguration = chatUiConfiguration;
        this.urlButtons = urlButtons;
        this.onMetadataItemClickListener = onMetadataItemClickListener;
    }

    @NonNull
    @Override
    public UrlButtonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent, chatUiConfiguration);
    }

    @Override
    public void onBindViewHolder(@NonNull UrlButtonAdapter.ViewHolder holder, int position) {
        holder.bind(urlButtons.get(position));
    }

    @Override
    public int getItemCount() {
        return urlButtons.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ChatUiConfiguration chatUiConfiguration;
        private final TextView text;
        private UrlButton urlButton;

        ViewHolder(final ViewGroup parent, final ChatUiConfiguration chatUiConfiguration) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.fcm_client_item_quick_reply, null));
            this.chatUiConfiguration = chatUiConfiguration;
            this.text = itemView.findViewById(R.id.text);
            this.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMetadataItemClickListener.onClickUrlButton(urlButton.getUrl());
                }
            });
            setupBackground(text);
        }

        private void setupBackground(TextView view) {
            int metadataBackground = chatUiConfiguration.getMetadataBackground();
            int metadataBackgroundColor = chatUiConfiguration.getMetadataBackgroundColor();

            if (metadataBackground != INVALID_VALUE) {
                view.setBackgroundResource(metadataBackground);
            }
            if (metadataBackgroundColor != INVALID_VALUE) {
                view.getBackground().setColorFilter(metadataBackgroundColor, PorterDuff.Mode.SRC_IN);
            }
        }

        void bind(UrlButton urlButton) {
            this.urlButton = urlButton;
            text.setText(urlButton.getTitle());
        }

    }

}
