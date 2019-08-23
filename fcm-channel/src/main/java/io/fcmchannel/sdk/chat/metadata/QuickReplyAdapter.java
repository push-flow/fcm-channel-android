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
import io.fcmchannel.sdk.ui.ChatUiConfiguration;

import static io.fcmchannel.sdk.ui.UiConfiguration.INVALID_VALUE;

/**
 * Created by john-mac on 6/30/16.
 */
public class QuickReplyAdapter extends RecyclerView.Adapter<QuickReplyAdapter.ViewHolder> {

    private ChatUiConfiguration chatUiConfiguration;
    private List<String> quickReplies;
    private OnMetadataItemClickListener onMetadataItemClickListener;

    public QuickReplyAdapter(
            ChatUiConfiguration chatUiConfiguration,
            List<String> quickReplies,
            OnMetadataItemClickListener onMetadataItemClickListener
    ) {
        this.chatUiConfiguration = chatUiConfiguration;
        this.quickReplies = quickReplies;
        this.onMetadataItemClickListener = onMetadataItemClickListener;
    }

    @NonNull
    @Override
    public QuickReplyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent, chatUiConfiguration);
    }

    @Override
    public void onBindViewHolder(@NonNull QuickReplyAdapter.ViewHolder holder, int position) {
        holder.bind(quickReplies.get(position));
    }

    @Override
    public int getItemCount() {
        return quickReplies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ChatUiConfiguration chatUiConfiguration;
        private final TextView text;
        private String quickReply;

        ViewHolder(final ViewGroup parent, final ChatUiConfiguration chatUiConfiguration) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.fcm_client_item_quick_reply, null));
            this.chatUiConfiguration = chatUiConfiguration;
            this.text = itemView.findViewById(R.id.text);
            this.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMetadataItemClickListener.onClickQuickReply(quickReply);
                    parent.setVisibility(View.GONE);
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

        void bind(String quickReply) {
            this.quickReply = quickReply;
            text.setText(quickReply);
        }

    }

}
