package io.fcmchannel.sdk.chat;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.fcmchannel.sdk.FcmClient;
import io.fcmchannel.sdk.UiConfiguration;
import io.fcmchannel.sdk.chat.metadata.OnMetadataItemClickListener;
import io.fcmchannel.sdk.core.models.Message;

/**
 * Created by johncordeiro on 7/21/15.
 */
class ChatMessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_TEXT_MESSAGES = 1;

    private List<Message> chatMessages;
    private final int iconResource;

    private ChatMessageViewHolder.OnChatMessageSelectedListener onChatMessageSelectedListener;
    private final OnMetadataItemClickListener onMetadataItemClickListener;

    ChatMessagesAdapter(OnMetadataItemClickListener onMetadataItemClickListener) {
        this.onMetadataItemClickListener = onMetadataItemClickListener;
        this.chatMessages = new ArrayList<>();
        this.iconResource = FcmClient.getUiConfiguration().getIconResource() != UiConfiguration.INVALID_VALUE ?
                FcmClient.getUiConfiguration().getIconResource() : FcmClient.getAppIcon();
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatMessageViewHolder(parent.getContext(), parent, iconResource);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessageViewHolder chatMessageViewHolder = ((ChatMessageViewHolder) holder);
        chatMessageViewHolder.setOnChatMessageSelectedListener(onChatMessageSelectedListener);
        chatMessageViewHolder.setOnMetadataItemClickListener(onMetadataItemClickListener);
        chatMessageViewHolder.setIsRecent(position == 0);
        chatMessageViewHolder.bindView(getItem(position));
    }

    private Message getItem(int position) {
        return chatMessages.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_TEXT_MESSAGES;
    }

    @Override
    public long getItemId(int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_TEXT_MESSAGES:
                Message chatMessage = getItem(position);
                return chatMessage.getId() != null ? chatMessage.getId().hashCode() : 0;
            default:
                return super.getItemId(position);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    Message getLastMessage() {
        return chatMessages.isEmpty() ? null : chatMessages.get(0);
    }

    void setMessages(List<Message> messages) {
        this.chatMessages = messages;
        notifyDataSetChanged();
    }

    void addChatMessage(Message message) {
        int position = chatMessages.indexOf(message);
        if (position >= 0) {
            chatMessages.set(position, message);
            notifyItemChanged(position);
        } else {
            chatMessages.add(0, message);
            notifyItemInserted( 0);
        }
        notifyDataSetChanged();
    }

    public void removeChatMessage(Message message) {
        int indexOfMessage = chatMessages.indexOf(message);
        if (indexOfMessage >= 0) {
            chatMessages.remove(indexOfMessage);
            notifyItemRemoved(indexOfMessage);
        }
    }

    public void setOnChatMessageSelectedListener(ChatMessageViewHolder.OnChatMessageSelectedListener onChatMessageSelectedListener) {
        this.onChatMessageSelectedListener = onChatMessageSelectedListener;
    }
}
