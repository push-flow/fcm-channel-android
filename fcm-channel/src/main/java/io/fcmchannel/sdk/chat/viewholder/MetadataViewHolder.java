package io.fcmchannel.sdk.chat.viewholder;

import br.com.ilhasoft.support.recyclerview.adapters.ViewHolder;
import io.fcmchannel.sdk.databinding.FcmClientItemMetadataBinding;
import io.fcmchannel.sdk.ui.ChatUiConfiguration;

public class MetadataViewHolder extends ViewHolder<String> {

    private FcmClientItemMetadataBinding binding;

    MetadataViewHolder(FcmClientItemMetadataBinding binding, ChatUiConfiguration uiConfig) {
        super(binding.getRoot());
        this.binding = binding;

        binding.setUiConfig(uiConfig);
    }

    @Override
    protected void onBind(String metadata) {
        binding.setMetadata(metadata);
        binding.executePendingBindings();
    }

}
