package io.fcmchannel.sdk.chat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;

import io.fcmchannel.sdk.FcmClient;
import io.fcmchannel.sdk.R;
import io.fcmchannel.sdk.chat.metadata.OnMetadataItemClickListener;
import io.fcmchannel.sdk.chat.metadata.QuickReplyAdapter;
import io.fcmchannel.sdk.chat.metadata.UrlButtonAdapter;
import io.fcmchannel.sdk.core.models.Message;
import io.fcmchannel.sdk.ui.ChatUiConfiguration;
import io.fcmchannel.sdk.util.SpaceItemDecoration;

import static io.fcmchannel.sdk.ui.UiConfiguration.INVALID_VALUE;

/**
 * Created by john-mac on 4/11/16.
 */
class ChatMessageViewHolder extends RecyclerView.ViewHolder {

    private Message chatMessage;

    private ViewGroup parent;

    private TextView message;
    private TextView date;
    private ImageView icon;
    private RecyclerView metadataList;

    private OnChatMessageSelectedListener onChatMessageSelectedListener;
    private OnMetadataItemClickListener onMetadataItemClickListener;

    private boolean isRecent;

    private final DateFormat hourFormatter;
    private final Context context;
    private final ChatUiConfiguration chatUiConfiguration;

    private final int leftMarginIncoming;
    private final int leftMarginOutgoing;
    private final int rightMarginIncoming;
    private final int rightMarginOutgoing;

    ChatMessageViewHolder(Context context, ViewGroup parent, ChatUiConfiguration chatUiConfiguration) {
        super(LayoutInflater.from(context).inflate(R.layout.fcm_client_item_chat_message, parent, false));
        this.context = context;
        this.chatUiConfiguration = chatUiConfiguration;
        this.hourFormatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        this.parent = itemView.findViewById(R.id.bubble);

        this.message = itemView.findViewById(R.id.chatMessage);
        this.date = itemView.findViewById(R.id.chatMessageDate);

        setupReceivedMessageIcon();

        this.metadataList = itemView.findViewById(R.id.metadataList);

        SpaceItemDecoration metadataItemDecoration = new SpaceItemDecoration();
        metadataItemDecoration.setHorizontalSpaceWidth(parent.getPaddingBottom());

        this.metadataList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        this.metadataList.addItemDecoration(metadataItemDecoration);

        this.itemView.setOnLongClickListener(onLongClickListener);

        this.leftMarginIncoming = getDpDimen(48);
        this.rightMarginIncoming = getDpDimen(8);
        this.leftMarginOutgoing = getDpDimen(40);
        this.rightMarginOutgoing = getDpDimen(48);
    }

    private void setupReceivedMessageIcon() {
        icon = itemView.findViewById(R.id.icon);
        int receivedMessageIconRes = this.chatUiConfiguration.getReceivedMessageIcon();

        icon.setImageResource(receivedMessageIconRes != INVALID_VALUE
                ? receivedMessageIconRes : FcmClient.getAppIcon());

        if (chatUiConfiguration.isReceivedMessageInTopDirection()) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) icon.getLayoutParams();
            layoutParams.gravity = Gravity.TOP;
        }
    }

    void bindView(Message chatMessage) {
        this.chatMessage = chatMessage;

        message.setText(chatMessage.getText());
        Linkify.addLinks(message, Linkify.ALL);
        date.setText(hourFormatter.format(chatMessage.getCreatedOn()));

        bindContainer(chatMessage);
    }

    private void bindContainer(Message chatMessage) {
        boolean incoming = isIncoming(chatMessage);
        icon.setVisibility(incoming ? View.GONE : View.VISIBLE);
        setupMetadataItem();

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) parent.getLayoutParams();
        setupBubblePosition(incoming, params);
        parent.setLayoutParams(params);

        setupMessageBackground(parent, incoming);
        setupMessageBackgroundColor(parent.getBackground(), incoming);
        setupMessageTextColor(message, incoming);
        setupMessageHourTextColor(date, incoming);
    }

    private void setupMessageBackground(View message, boolean incoming) {
        if (incoming) {
            int sentMessageBackgroundRes = chatUiConfiguration.getSentMessageBackground();

            if (sentMessageBackgroundRes != INVALID_VALUE)
                message.setBackgroundResource(sentMessageBackgroundRes);
            else if (chatUiConfiguration.isSentMessageInTopDirection())
                message.setBackgroundResource(R.drawable.fcm_client_bubble_me_top);
            else
                message.setBackgroundResource(R.drawable.fcm_client_bubble_me);
        } else {
            int receivedMessageBackgroundRes = chatUiConfiguration.getReceivedMessageBackground();

            if (receivedMessageBackgroundRes != INVALID_VALUE)
                message.setBackgroundResource(receivedMessageBackgroundRes);
            else if (chatUiConfiguration.isReceivedMessageInTopDirection())
                message.setBackgroundResource(R.drawable.fcm_client_bubble_other_top);
            else
                message.setBackgroundResource(R.drawable.fcm_client_bubble_other);
        }
    }

    private void setupMessageBackgroundColor(Drawable background, boolean incoming) {
        int sentMessageBackgroundColor = chatUiConfiguration.getSentMessageBackgroundColor();
        int receivedMessageBackgroundColor = chatUiConfiguration.getReceivedMessageBackgroundColor();

        if (incoming && sentMessageBackgroundColor != INVALID_VALUE) {
            background.setColorFilter(sentMessageBackgroundColor, PorterDuff.Mode.SRC_IN);
        } else if (!incoming && receivedMessageBackgroundColor != INVALID_VALUE) {
            background.setColorFilter(receivedMessageBackgroundColor, PorterDuff.Mode.SRC_IN);
        }
    }

    private void setupMessageTextColor(TextView text, boolean incoming) {
        if (incoming) {
            int sentMessageTextColor = chatUiConfiguration.getSentMessageTextColor();
            text.setTextColor(sentMessageTextColor != INVALID_VALUE ? sentMessageTextColor : Color.BLACK);
        } else {
            int receivedMessageTextColor = chatUiConfiguration.getReceivedMessageTextColor();
            text.setTextColor(receivedMessageTextColor != INVALID_VALUE ? receivedMessageTextColor : Color.WHITE);
        }
    }

    private void setupMessageHourTextColor(TextView hour, boolean incoming) {
        int sentMessageHourTextColor = chatUiConfiguration.getSentMessageHourTextColor();
        int receivedMessageHourTextColor = chatUiConfiguration.getReceivedMessageHourTextColor();

        if (incoming && sentMessageHourTextColor != INVALID_VALUE) {
            hour.setTextColor(sentMessageHourTextColor);
        } else if (receivedMessageHourTextColor != INVALID_VALUE) {
            hour.setTextColor(receivedMessageHourTextColor);
        }
    }

    private void setupBubblePosition(boolean incoming, FrameLayout.LayoutParams params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.gravity = incoming ? Gravity.END : Gravity.START;
            params.setMarginStart(incoming ? leftMarginIncoming : leftMarginOutgoing);
            params.setMarginEnd(incoming ? rightMarginIncoming : rightMarginOutgoing);
        } else {
            params.gravity = incoming ? Gravity.RIGHT : Gravity.LEFT;
            params.leftMargin = incoming ? leftMarginIncoming : leftMarginOutgoing;
            params.rightMargin = incoming ? rightMarginIncoming : rightMarginOutgoing;
        }
    }

    private boolean isIncoming(Message chatMessage) {
        return chatMessage.getDirection().equals(Message.DIRECTION_INCOMING);
    }

    private int getDpDimen(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value
                , context.getResources().getDisplayMetrics());
    }

    private void setupMetadataItem() {
        if (checkHasQuickReplies() && isRecent) {
            metadataList.setAdapter(new QuickReplyAdapter(
                    chatUiConfiguration,
                    chatMessage.getMetadata().getQuickReplies(),
                    onMetadataItemClickListener
            ));
            metadataList.setVisibility(View.VISIBLE);
        } else if (checkHasUrlButtons()) {
            metadataList.setAdapter(new UrlButtonAdapter(
                    chatUiConfiguration,
                    chatMessage.getMetadata().getUrlButtons(),
                    onMetadataItemClickListener));
            metadataList.setVisibility(View.VISIBLE);
        } else {
            metadataList.setVisibility(View.GONE);
        }
    }

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            if (onChatMessageSelectedListener != null) {
                onChatMessageSelectedListener.onChatMessageSelected(chatMessage);
            }
            return false;
        }
    };

    void setIsRecent(boolean isRecent) {
        this.isRecent = isRecent;
    }

    void setOnChatMessageSelectedListener(OnChatMessageSelectedListener onChatMessageSelectedListener) {
        this.onChatMessageSelectedListener = onChatMessageSelectedListener;
    }

    interface OnChatMessageSelectedListener {
        void onChatMessageSelected(Message chatMessage);
    }

    void setOnMetadataItemClickListener(OnMetadataItemClickListener onMetadataItemClickListener) {
        this.onMetadataItemClickListener = onMetadataItemClickListener;
    }

    private boolean checkHasQuickReplies() {
        return chatMessage.getMetadata() != null &&
                chatMessage.getMetadata().getQuickReplies() != null &&
                chatMessage.getMetadata().getQuickReplies().size() > 0;
    }

    private boolean checkHasUrlButtons() {
        return chatMessage.getMetadata() != null &&
                chatMessage.getMetadata().getUrlButtons() != null &&
                chatMessage.getMetadata().getUrlButtons().size() > 0;
    }

}
