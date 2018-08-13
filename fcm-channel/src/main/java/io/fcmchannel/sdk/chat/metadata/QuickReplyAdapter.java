package io.fcmchannel.sdk.chat.metadata;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.fcmchannel.sdk.R;

/**
 * Created by john-mac on 6/30/16.
 */
public class QuickReplyAdapter extends RecyclerView.Adapter<QuickReplyAdapter.ViewHolder> {

    private List<String> quickReplies;
    private OnMetadataItemClickListener onMetadataItemClickListener;

    public QuickReplyAdapter(List<String> quickReplies, OnMetadataItemClickListener onMetadataItemClickListener) {
        this.quickReplies = quickReplies;
        this.onMetadataItemClickListener = onMetadataItemClickListener;
    }

    @Override
    public QuickReplyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(QuickReplyAdapter.ViewHolder holder, int position) {
        holder.bind(quickReplies.get(position));
    }

    @Override
    public int getItemCount() {
        return quickReplies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView text;
        private String quickReply;

        ViewHolder(final ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.fcm_client_item_quick_reply, null));
            text = (TextView) itemView.findViewById(R.id.text);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMetadataItemClickListener.onClickQuickReply(quickReply);
                    parent.setVisibility(View.GONE);
                }
            });
        }

        void bind(String quickReply) {
            this.quickReply = quickReply;
            text.setText(quickReply);
        }

    }

}
