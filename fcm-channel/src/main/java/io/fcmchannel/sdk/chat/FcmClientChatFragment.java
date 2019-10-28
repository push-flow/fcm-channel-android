package io.fcmchannel.sdk.chat;

import android.animation.LayoutTransition;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.ilhasoft.support.recyclerview.adapters.AutoRecyclerAdapter;
import br.com.ilhasoft.support.recyclerview.adapters.OnCreateViewHolder;
import io.fcmchannel.sdk.FcmClient;
import io.fcmchannel.sdk.R;
import io.fcmchannel.sdk.chat.metadata.OnMetadataItemClickListener;
import io.fcmchannel.sdk.chat.viewholder.ChatViewHolder;
import io.fcmchannel.sdk.chat.viewholder.LoadingViewHolder;
import io.fcmchannel.sdk.chat.viewholder.MessageViewHolder;
import io.fcmchannel.sdk.core.models.Message;
import io.fcmchannel.sdk.databinding.FcmClientItemChatMessageBinding;
import io.fcmchannel.sdk.databinding.FcmClientItemLoadingBinding;
import io.fcmchannel.sdk.services.FcmClientIntentService;
import io.fcmchannel.sdk.ui.ChatUiConfiguration;
import io.fcmchannel.sdk.util.SpaceItemDecoration;

import static io.fcmchannel.sdk.ui.UiConfiguration.INVALID_VALUE;

/**
 * Created by john-mac on 8/30/16.
 */
public class FcmClientChatFragment extends Fragment implements FcmClientChatView {

    private static final int VIEW_TYPE_MESSAGE = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private ChatUiConfiguration chatUiConfiguration = FcmClient.getUiConfiguration().getChatUiConfiguration();
    private EditText message;
    private RecyclerView messagesList;

    private AutoRecyclerAdapter<Message, ChatViewHolder> messagesAdapter;
    private FcmClientChatPresenter presenter;

    public static boolean visible = false;

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        cleanUnreadMessages();
        return inflater.inflate(R.layout.fcm_client_fragment_chat, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cleanUnreadMessages();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
        presenter = chatUiConfiguration.messagePagingEnabled()
                ? new FcmClientChatPresenter(this, chatUiConfiguration.getMessagePageSize())
                : new FcmClientChatPresenter(this);

        loadMessages();
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        registerBroadcasts(getContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterBroadcasts(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        visible = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        visible = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        visible = isVisibleToUser;
    }

    @Override
    public void showLoading() { }

    @Override
    public void dismissLoading() { }

    @Override
    public void showMessage(int messageId) {
        showMessage(requireContext().getString(messageId));
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessagesLoaded(List<Message> messages) {
        dismissListLoading();
        messagesAdapter.addAll(messages);

        if (!messages.isEmpty()) showLoading();
    }

    @Override
    public void onMessageLoaded(Message message) {
        int position = messagesAdapter.indexOf(message);
        if (position >= 0) {
            messagesAdapter.set(position, message);
        } else {
            messagesAdapter.add(0, message);
            messagesAdapter.notifyItemChanged(1);
        }
        onLastMessageChanged();
    }

    @Override
    public Message getLastMessage() {
        return messagesAdapter.isEmpty() ? null : messagesAdapter.get(0);
    }

    @Override
    public void addNewMessage(String messageText) {
        restoreView();
        onMessageLoaded(presenter.createChatMessage(messageText));
    }

    private void cleanUnreadMessages() {
        FcmClient.getPreferences().setUnreadMessages(0).commit();
    }

    private void setupView(View view) {
        RelativeLayout container = view.findViewById(R.id.container);
        LayoutTransition transition = new LayoutTransition();
        transition.setDuration(500);
        container.setLayoutTransition(transition);

        message = view.findViewById(R.id.message);
        messagesList = view.findViewById(R.id.messageList);
        setupMessagesList();

        ImageView sendMessage = view.findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(onSendMessageClickListener);

        int sendMessageIconColor = chatUiConfiguration.getSendMessageButtonColor();
        if (sendMessageIconColor != INVALID_VALUE) {
            sendMessage.setColorFilter(sendMessageIconColor, PorterDuff.Mode.SRC_IN);
        }
    }

    private void setupMessagesList() {
        messagesAdapter = new AutoRecyclerAdapter<Message, ChatViewHolder>(onCreateViewHolder) {
            @Override
            public int getItemViewType(int position) {
                if (messagesAdapter.get(position) != null) {
                    return VIEW_TYPE_MESSAGE;
                } else {
                    return VIEW_TYPE_LOADING;
                }
            }
        };
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
            getContext(),
            LinearLayoutManager.VERTICAL,
            true
        );
        SpaceItemDecoration itemDecoration = new SpaceItemDecoration();
        int spaceHeight = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            8,
            getResources().getDisplayMetrics()
        );
        itemDecoration.setVerticalSpaceHeight(spaceHeight);

        messagesList.setLayoutManager(layoutManager);
        messagesList.setAdapter(messagesAdapter);
        messagesList.addItemDecoration(itemDecoration);

        showListLoading();
    }

    private void loadMessages() {
        if (chatUiConfiguration.messagePagingEnabled()) {
            presenter.loadMessagesPaginated();
        } else {
            presenter.loadAllMessages();
        }
    }

    public void registerBroadcasts(Context context) {
        if (context != null) {
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);

            IntentFilter messagesBroadcastFilter = new IntentFilter(FcmClientIntentService.ACTION_MESSAGE_RECEIVED);
            localBroadcastManager.registerReceiver(messagesReceiver, messagesBroadcastFilter);
        }
    }

    public void unregisterBroadcasts(Context context) {
        try {
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
            localBroadcastManager.unregisterReceiver(messagesReceiver);
        } catch (Exception exception) {
            Log.e("FcmClientChat", "onStop: ", exception);
        }
    }

    private void onLastMessageChanged() {
        messagesList.scrollToPosition(0);
    }

    private void restoreView() {
        message.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        message.setError(null);
        message.setText(null);
    }

    private void showListLoading() {
        if (messagesAdapter.isEmpty() || messagesAdapter.get(messagesAdapter.size() - 1) != null) {
            messagesAdapter.add(null);
        }
    }

    private void dismissListLoading() {
        if (!messagesAdapter.isEmpty() && messagesAdapter.get(messagesAdapter.size() - 1) == null) {
            messagesAdapter.remove(messagesAdapter.size() - 1);
        }
    }

    private BroadcastReceiver messagesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data = intent.getBundleExtra(FcmClientIntentService.KEY_DATA);
            presenter.loadMessage(data);
        }
    };

    private View.OnClickListener onSendMessageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String messageText = message.getText().toString();
            if (!messageText.isEmpty()) {
                presenter.sendMessage(messageText);
            } else {
                message.setError(requireContext().getString(R.string.fcm_client_error_send_message));
            }
        }
    };
    private OnCreateViewHolder<Message, ChatViewHolder> onCreateViewHolder = new OnCreateViewHolder<Message, ChatViewHolder>() {
        @Override
        public ChatViewHolder onCreateViewHolder(
            LayoutInflater layoutInflater,
            ViewGroup parent,
            int viewType
        ) {
            if (viewType == VIEW_TYPE_MESSAGE) {
                return new MessageViewHolder(
                    FcmClientItemChatMessageBinding.inflate(layoutInflater, parent, false),
                    chatUiConfiguration
                );
            } else {
                return new LoadingViewHolder(
                    FcmClientItemLoadingBinding.inflate(layoutInflater, parent, false),
                    chatUiConfiguration
                );
            }
        }
    };
    private OnMetadataItemClickListener onMetadataItemClickListener = new OnMetadataItemClickListener() {
        @Override
        public void onClickQuickReply(String reply) {
            presenter.sendMessage(reply);
        }

        @Override
        public void onClickUrlButton(String url) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    };
    private OnDemandListener onDemandListener = new OnDemandListener() {
        @Override
        public void onLoadMore() {
            presenter.loadMessagesPaginated();
        }
    };
}
