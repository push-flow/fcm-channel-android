package io.fcmchannel.sdk.chat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.fcmchannel.sdk.chat.metadata.OnMetadataItemClickListener;
import io.fcmchannel.sdk.core.models.Message;
import io.fcmchannel.sdk.ui.ChatUiConfiguration;

/**
 * Created by johncordeiro on 7/21/15.
 */
class ChatMessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_TEXT_MESSAGES = 1;
    private static final int VIEW_TYPE_LOADING = 2;

    private ChatUiConfiguration chatUiConfiguration;
    private List<Message> chatMessages;

    private ChatMessageViewHolder.OnChatMessageSelectedListener onChatMessageSelectedListener;
    private final OnMetadataItemClickListener onMetadataItemClickListener;
    private OnDemandListener onDemandListener;

    ChatMessagesAdapter(
            ChatUiConfiguration chatUiConfiguration,
            OnMetadataItemClickListener onMetadataItemClickListener
    ) {
        this.chatUiConfiguration = chatUiConfiguration;
        this.onMetadataItemClickListener = onMetadataItemClickListener;
        this.chatMessages = new ArrayList<>();
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_TEXT_MESSAGES) {
            return new ChatMessageViewHolder(parent, chatUiConfiguration);
        } else {
            return new LoadingViewHolder(parent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == chatMessages.size() - 1 && onDemandListener != null) {
            onDemandListener.onLoadMore();
        }
        if (chatMessages.get(position) == null) {
            return;
        }
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
        if (chatMessages.get(position) != null) {
            return VIEW_TYPE_TEXT_MESSAGES;
        } else {
            return VIEW_TYPE_LOADING;
        }
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

    void addMessages(List<Message> messages) {
        if (isListLoadingItemEnabled()) {
            dismissLoading();
        }
        this.chatMessages.addAll(messages);
        notifyItemRangeInserted(chatMessages.size() - messages.size(), messages.size());

        if (isListLoadingItemEnabled() && !messages.isEmpty()) {
            showLoading();
        }
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

    public void setOnDemandListener(OnDemandListener onDemandListener) {
        this.onDemandListener = onDemandListener;
    }

    public void setOnChatMessageSelectedListener(ChatMessageViewHolder.OnChatMessageSelectedListener onChatMessageSelectedListener) {
        this.onChatMessageSelectedListener = onChatMessageSelectedListener;
    }

    private boolean isListLoadingItemEnabled() {
        return chatUiConfiguration.getMessagesPageSize() > 0;
    }

    public void showLoading() {
        if (chatMessages.isEmpty() || chatMessages.get(chatMessages.size() - 1) != null) {
            chatMessages.add(null);
            notifyItemInserted(chatMessages.size() - 1);
        }
    }

    public void dismissLoading() {
        if (!chatMessages.isEmpty() && chatMessages.get(chatMessages.size() - 1) == null) {
            chatMessages.remove(chatMessages.size() - 1);
            notifyItemRemoved(chatMessages.size());
        }
    }

}
