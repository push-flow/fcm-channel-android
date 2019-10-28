package io.fcmchannel.sdk.chat.viewholder;

import android.view.View;

import br.com.ilhasoft.support.recyclerview.adapters.ViewHolder;
import io.fcmchannel.sdk.core.models.Message;

public abstract class ChatViewHolder extends ViewHolder<Message> {

    ChatViewHolder(View itemView) {
        super(itemView);
    }

}
