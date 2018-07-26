package io.fcmchannel.sdk.chat.metadata;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.fcmchannel.sdk.R;
import io.fcmchannel.sdk.core.models.QuickReply;

/**
 * Created by john-mac on 6/30/16.
 */
public class QuickReplyAdapter extends RecyclerView.Adapter<QuickReplyAdapter.ViewHolder> {

    private List<QuickReply> quickReplies;
    private OnQuickReplyClickListener onQuickReplyClickListener;

    public QuickReplyAdapter(List<QuickReply> quickReplies, OnQuickReplyClickListener onQuickReplyClickListener) {
        this.quickReplies = quickReplies;
        this.onQuickReplyClickListener = onQuickReplyClickListener;
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
        private QuickReply quickReply;

        ViewHolder(final ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.fcm_client_item_quick_reply, null));
            text = (TextView) itemView.findViewById(R.id.text);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onQuickReplyClickListener.onClick(quickReply.getTitle());
                    parent.setVisibility(View.GONE);
                }
            });
        }

        void bind(QuickReply quickReply) {
            this.quickReply = quickReply;
            text.setText(quickReply.getTitle());
        }

    }

}
