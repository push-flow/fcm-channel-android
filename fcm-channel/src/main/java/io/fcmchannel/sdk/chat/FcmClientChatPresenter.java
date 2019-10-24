package io.fcmchannel.sdk.chat;

import android.os.Bundle;
import android.text.TextUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.fcmchannel.sdk.FcmClient;
import io.fcmchannel.sdk.R;
import io.fcmchannel.sdk.core.models.Message;
import io.fcmchannel.sdk.core.models.network.ApiResponse;
import io.fcmchannel.sdk.core.models.Contact;
import io.fcmchannel.sdk.core.network.RestServices;
import io.fcmchannel.sdk.util.BundleHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by john-mac on 6/30/16.
 */
class FcmClientChatPresenter {

    private final FcmClientChatView view;
    private final RestServices services;

    private String nextPageCursor;
    private int messagesPageSize;

    private boolean isLoadingMessages;
    private boolean allMessagesWereLoaded;

    FcmClientChatPresenter(FcmClientChatView view) {
        this.view = view;
        this.services = new RestServices(FcmClient.getHost(), FcmClient.getToken());
    }

    FcmClientChatPresenter(FcmClientChatView view, int messagesPageSize) {
        this(view);
        this.messagesPageSize = messagesPageSize;
    }

    void loadMessages() {
        if (FcmClient.isContactRegistered()) {
            String contactUuid = FcmClient.getContact().getUuid();
            if (!TextUtils.isEmpty(contactUuid)) {
                loadMessages(contactUuid);
            } else {
                loadContact(false);
            }
        }
    }

    void loadMessagesPaginated() {
        if (isLoadingMessages || allMessagesWereLoaded) return;

        if (FcmClient.isContactRegistered()) {
            String contactUuid = FcmClient.getContact().getUuid();
            if (!TextUtils.isEmpty(contactUuid)) {
                loadMessagesPaginated(contactUuid);
            } else {
                loadContact(true);
            }
        }
    }

    private void loadContact(final boolean pagedLoading) {
        view.showLoading();

        String urn = FcmClient.URN_PREFIX_FCM + FcmClient.getPreferences().getUrn();
        services.loadContact(urn).enqueue(new FcmClientCallback<ApiResponse<Contact>>(this) {
            @Override
            public void onResponse(Call<ApiResponse<Contact>> call, Response<ApiResponse<Contact>> response) {
                view.dismissLoading();

                if (response.isSuccessful() && response.body() != null && response.body().getResults() != null) {
                    Contact contact = response.body().getResults().get(0);

                    if (pagedLoading) {
                        loadMessagesPaginated(contact.getUuid());
                    } else {
                        loadMessages(contact.getUuid());
                    }
                } else {
                    view.showMessage(R.string.fcm_client_error_load_messages);
                }
            }
        });
    }

    void onRequestFailed() {
        view.dismissLoading();
        view.showMessage(R.string.fcm_client_error_load_messages);
    }

    private void loadMessages(String contactUuid) {
        view.showLoading();
        services.loadMessages(contactUuid).enqueue(new Callback<ApiResponse<Message>>() {
            @Override
            public void onResponse(Call<ApiResponse<Message>> call, Response<ApiResponse<Message>> response) {
                view.dismissLoading();
                if (response.isSuccessful()) {
                    onMessagesLoaded(response.body().getResults());
                } else {
                    view.showMessage(R.string.fcm_client_error_load_messages);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Message>> call, Throwable throwable) {
                throwable.printStackTrace();
                onRequestFailed();
            }
        });
    }

    private void loadMessagesPaginated(String contactUuid) {
        isLoadingMessages = true;

        services.loadMessages(contactUuid, nextPageCursor, messagesPageSize).enqueue(new Callback<ApiResponse<Message>>() {
            @Override
            public void onResponse(Call<ApiResponse<Message>> call, Response<ApiResponse<Message>> response) {
                if (response.isSuccessful()) {
                    onMessagesLoaded(response.body().getResults());
                    String next = response.body().getNext();

                    if (next != null) {
                        setNextPageCursor(next);
                    } else {
                        allMessagesWereLoaded = true;
                        onMessagesLoaded(Collections.<Message>emptyList());
                    }
                } else {
                    view.showMessage(R.string.fcm_client_error_load_messages);
                }
                isLoadingMessages = false;
            }

            @Override
            public void onFailure(Call<ApiResponse<Message>> call, Throwable throwable) {
                throwable.printStackTrace();
                onRequestFailed();
                isLoadingMessages = false;
            }
        });
    }

    private void setNextPageCursor(String nextPageUrl) {
        String cursorQuery = "cursor=";
        int startIndex = nextPageUrl.indexOf(cursorQuery) + cursorQuery.length();
        int endIndex = nextPageUrl.indexOf("&", startIndex);
        endIndex = endIndex == -1 ? nextPageUrl.length() : endIndex;
        nextPageCursor = nextPageUrl.substring(startIndex, endIndex);
    }

    private void onMessagesLoaded(List<Message> messages) {
        view.onMessagesLoaded(messages);
    }

    void loadMessage(Bundle data) {
        Message message = BundleHelper.getMessage(data);
        message.setCreatedOn(new Date());
        view.onMessageLoaded(message);
    }

    Message createChatMessage(String messageText) {
        Message chatMessage = new Message();
        setId(chatMessage);
        chatMessage.setText(messageText);
        chatMessage.setCreatedOn(new Date());
        chatMessage.setDirection(Message.DIRECTION_INCOMING);
        return chatMessage;
    }

    private void setId(Message chatMessage) {
        Message lastMessage = view.getLastMessage();
        if (lastMessage != null) {
            chatMessage.setId(lastMessage.getId()+1);
        } else {
            chatMessage.setId(0);
        }
    }

    public void sendMessage(String messageText) {
        view.addNewMessage(messageText);
        FcmClient.sendMessage(messageText);
    }
}
