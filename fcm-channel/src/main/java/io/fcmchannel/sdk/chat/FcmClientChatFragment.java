package io.fcmchannel.sdk.chat;

import android.animation.LayoutTransition;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import io.fcmchannel.sdk.FcmClient;
import io.fcmchannel.sdk.R;
import io.fcmchannel.sdk.chat.metadata.OnMetadataItemClickListener;
import io.fcmchannel.sdk.core.models.Message;
import io.fcmchannel.sdk.services.FcmClientIntentService;
import io.fcmchannel.sdk.services.FcmClientRegistrationIntentService;
import io.fcmchannel.sdk.ui.ChatUiConfiguration;
import io.fcmchannel.sdk.util.SpaceItemDecoration;

import static io.fcmchannel.sdk.ui.UiConfiguration.INVALID_VALUE;

/**
 * Created by john-mac on 8/30/16.
 */
public class FcmClientChatFragment extends Fragment implements FcmClientChatView {

    private ChatUiConfiguration chatUiConfiguration;
    private EditText message;
    private RecyclerView messageList;
    private ProgressBar progressBar;

    private ChatMessagesAdapter adapter;
    private FcmClientChatPresenter presenter;

    public static boolean visible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        if (chatUiConfiguration.getMessagesPageSize() > 0) {
            presenter = new FcmClientChatPresenter(this, chatUiConfiguration.getMessagesPageSize());
            presenter.loadMessagesPaginated();
        } else {
            presenter = new FcmClientChatPresenter(this);
            presenter.loadMessages();
        }
    }

    private void cleanUnreadMessages() {
        FcmClient.getPreferences().setUnreadMessages(0).commit();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        visible = isVisibleToUser;
    }

    @Override
    public void onPause() {
        super.onPause();
        visible = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        visible = true;
    }

    private void setupView(View view) {
        chatUiConfiguration = FcmClient.getUiConfiguration().getChatUiConfiguration();

        setupChatBackground((ImageView) view.findViewById(R.id.background));

        RelativeLayout container = view.findViewById(R.id.container);
        LayoutTransition transition = new LayoutTransition();
        transition.setDuration(500);
        container.setLayoutTransition(transition);

        message = view.findViewById(R.id.message);
        adapter = new ChatMessagesAdapter(chatUiConfiguration, onMetadataItemClickListener);

        if (chatUiConfiguration.getMessagesPageSize() > 0) {
            adapter.setOnDemandListener(onDemandListener);
        }
        messageList = view.findViewById(R.id.messageList);
        messageList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));

        int spaceHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, view.getResources().getDisplayMetrics());
        SpaceItemDecoration messagesItemDecoration = new SpaceItemDecoration();
        messagesItemDecoration.setVerticalSpaceHeight(spaceHeight);

        messageList.addItemDecoration(messagesItemDecoration);
        messageList.setAdapter(adapter);

        ImageView sendMessage = view.findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(onSendMessageClickListener);

        int sendMessageIconColor = chatUiConfiguration.getSendMessageIconColor();
        if (sendMessageIconColor != INVALID_VALUE) {
            sendMessage.setColorFilter(sendMessageIconColor, PorterDuff.Mode.SRC_IN);
        }

        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupChatBackground(ImageView imageView) {
        int chatBackground = chatUiConfiguration.getChatBackground();
        int chatBackgroundImage = chatUiConfiguration.getChatBackgroundImage();

        if (chatBackgroundImage != INVALID_VALUE) {
            imageView.setBackgroundDrawable(null);
            imageView.setImageResource(chatBackgroundImage);
        } else if (chatBackground != INVALID_VALUE) {
            imageView.setBackgroundResource(chatBackground);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        registerBroadcasts(getContext());
    }

    public void registerBroadcasts(Context context) {
        if (context != null) {
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);

            IntentFilter registrationFilter = new IntentFilter(FcmClientRegistrationIntentService.REGISTRATION_COMPLETE);
            localBroadcastManager.registerReceiver(onRegisteredReceiver, registrationFilter);

            IntentFilter messagesBroadcastFilter = new IntentFilter(FcmClientIntentService.ACTION_MESSAGE_RECEIVED);
            localBroadcastManager.registerReceiver(messagesReceiver, messagesBroadcastFilter);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterBroadcasts(getContext());
    }

    public void unregisterBroadcasts(Context context) {
        try {
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
            localBroadcastManager.unregisterReceiver(messagesReceiver);
            localBroadcastManager.unregisterReceiver(onRegisteredReceiver);
        } catch (Exception exception) {
            Log.e("FcmClientChat", "onStop: ", exception);
        }
    }

    private BroadcastReceiver messagesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data = intent.getBundleExtra(FcmClientIntentService.KEY_DATA);
            presenter.loadMessage(data);
        }
    };

    @Override
    public void onMessagesLoaded(List<Message> messages) {
        adapter.addMessages(messages);
    }

    @Override
    public void onMessageLoaded(Message message) {
        adapter.addChatMessage(message);
        onLastMessageChanged();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(int messageId) {
        Context context = getContext();
        if (context != null) {
            showMessage(context.getString(messageId));
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Message getLastMessage() {
        return adapter.getLastMessage();
    }

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

    @Override
    public void addNewMessage(String messageText) {
        restoreView();

        adapter.addChatMessage(presenter.createChatMessage(messageText));
        messageList.scrollToPosition(0);
    }

    private void onLastMessageChanged() {
        messageList.scrollToPosition(0);
    }

    private void restoreView() {
        message.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        message.setError(null);
        message.setText(null);
    }

    private BroadcastReceiver onRegisteredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            presenter.loadMessages();
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
