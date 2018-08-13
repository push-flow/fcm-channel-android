package io.fcmchannel.sdk.chat.metadata;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.fcmchannel.sdk.R;
import io.fcmchannel.sdk.core.models.UrlButton;

public class UrlButtonAdapter extends RecyclerView.Adapter<UrlButtonAdapter.ViewHolder> {

    private List<UrlButton> urlButtons;
    private OnMetadataItemClickListener onMetadataItemClickListener;

    public UrlButtonAdapter(List<UrlButton> urlButtons, OnMetadataItemClickListener onMetadataItemClickListener) {
        this.urlButtons = urlButtons;
        this.onMetadataItemClickListener = onMetadataItemClickListener;
    }

    @Override
    public UrlButtonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(UrlButtonAdapter.ViewHolder holder, int position) {
        holder.bind(urlButtons.get(position));
    }

    @Override
    public int getItemCount() {
        return urlButtons.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView text;
        private UrlButton urlButton;

        ViewHolder(final ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.fcm_client_item_quick_reply, null));
            text = (TextView) itemView.findViewById(R.id.text);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMetadataItemClickListener.onClickUrlButton(urlButton.getUrl());
                }
            });
        }

        void bind(UrlButton urlButton) {
            this.urlButton = urlButton;
            text.setText(urlButton.getTitle());
        }

    }

}
