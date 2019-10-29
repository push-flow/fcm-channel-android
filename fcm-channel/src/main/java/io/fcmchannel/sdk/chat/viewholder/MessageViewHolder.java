package io.fcmchannel.sdk.chat.viewholder;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.ilhasoft.support.recyclerview.adapters.AutoRecyclerAdapter;
import br.com.ilhasoft.support.recyclerview.adapters.OnCreateViewHolder;
import io.fcmchannel.sdk.core.models.Message;
import io.fcmchannel.sdk.core.models.MessageMetadata;
import io.fcmchannel.sdk.core.models.UrlButton;
import io.fcmchannel.sdk.databinding.FcmClientItemChatMessageBinding;
import io.fcmchannel.sdk.databinding.FcmClientItemMetadataBinding;
import io.fcmchannel.sdk.ui.ChatUiConfiguration;
import io.fcmchannel.sdk.util.SpaceItemDecoration;

public class MessageViewHolder extends ChatViewHolder {

    private FcmClientItemChatMessageBinding binding;
    private ChatUiConfiguration uiConfig;
    private AutoRecyclerAdapter<String, MetadataViewHolder> metadataAdapter;

    public MessageViewHolder(FcmClientItemChatMessageBinding binding, ChatUiConfiguration uiConfig) {
        super(binding.getRoot());
        this.binding = binding;
        this.uiConfig = uiConfig;

        binding.setUiConfig(uiConfig);
        setupMetadataList();
    }

    @Override
    protected void onBind(Message message) {
        binding.setIsReceivedMessage(message.getDirection().equals(Message.DIRECTION_OUTGOING));
        binding.setText(message.getText());
        binding.setCreatedOn(uiConfig.getDateFormat().format(message.getCreatedOn()));
        binding.executePendingBindings();

        bindMetadata(message.getMetadata());
    }

    private void setupMetadataList() {
        metadataAdapter = new AutoRecyclerAdapter<>(
            new OnCreateViewHolder<String, MetadataViewHolder>() {
                @Override
                public MetadataViewHolder onCreateViewHolder(
                    LayoutInflater layoutInflater,
                    ViewGroup parent,
                    int viewType
                ) {
                    return new MetadataViewHolder(
                        FcmClientItemMetadataBinding.inflate(layoutInflater),
                        uiConfig
                    );
                }
            }
        );
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
            itemView.getContext(), LinearLayoutManager.HORIZONTAL, false
        );
        int spaceWidth = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            8,
            itemView.getResources().getDisplayMetrics()
        );
        SpaceItemDecoration itemDecoration = new SpaceItemDecoration();
        itemDecoration.setHorizontalSpaceWidth(spaceWidth);

        binding.metadataList.setLayoutManager(layoutManager);
        binding.metadataList.setAdapter(metadataAdapter);
        binding.metadataList.addItemDecoration(itemDecoration);
    }

    private void bindMetadata(MessageMetadata metadata) {
        metadataAdapter.clear();

        if (metadata == null) {
            binding.metadataList.setVisibility(View.GONE);
            return;
        }
        List<String> quickReplies = metadata.getQuickReplies();
        List<UrlButton> urlButtons = metadata.getUrlButtons();

        if (quickReplies != null && !quickReplies.isEmpty() && isFirstPosition()) {
            metadataAdapter.addAll(quickReplies);
            binding.metadataList.setVisibility(View.VISIBLE);
        } else if (urlButtons != null && !urlButtons.isEmpty()) {
            for (UrlButton urlButton : urlButtons) {
                metadataAdapter.add(urlButton.getTitle());
            }
            binding.metadataList.setVisibility(View.VISIBLE);
        } else {
            binding.metadataList.setVisibility(View.GONE);
        }
    }

    private boolean isFirstPosition() {
        return getAdapterPosition() == 0;
    }

}
