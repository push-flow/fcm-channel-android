package io.fcmchannel.sdk.chat;

import android.os.Bundle;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.fcmchannel.sdk.FcmClient;
import io.fcmchannel.sdk.R;
import io.fcmchannel.sdk.core.models.Contact;
import io.fcmchannel.sdk.core.models.Message;
import io.fcmchannel.sdk.core.models.network.ApiResponse;
import io.fcmchannel.sdk.core.network.RestServices;
import io.fcmchannel.sdk.util.BundleHelper;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by john-mac on 6/30/16.
 */
class FcmClientChatPresenter {

    private final WeakReference<FcmClientChatView> viewReference;
    private final RestServices services;
    private final CompositeDisposable disposables;

    private String nextPageCursor;
    private int messagesPageSize;

    private boolean isLoadingMessages;
    private boolean allMessagesWereLoaded;

    FcmClientChatPresenter(FcmClientChatView view) {
        this.viewReference = new WeakReference<>(view);
        this.services = new RestServices(FcmClient.getHost(), FcmClient.getToken());
        this.disposables = new CompositeDisposable();
    }

    FcmClientChatPresenter(FcmClientChatView view, int messagesPageSize) {
        this(view);
        this.messagesPageSize = messagesPageSize;
    }

    void detachView() {
        viewReference.clear();
    }

    FcmClientChatView getView() {
        return viewReference.get();
    }

    void loadAllMessages() {
        final Disposable disposable = getContactUUid()
            .subscribeOn(Schedulers.io())
            .flatMap(new Function<String, SingleSource<ApiResponse<Message>>>() {
                @Override
                public SingleSource<ApiResponse<Message>> apply(String contactUuid) {
                    return services.loadMessages(contactUuid);
                }
            })
            .flatMapObservable(new Function<ApiResponse<Message>, ObservableSource<List<Message>>>() {
                @Override
                public ObservableSource<List<Message>> apply(ApiResponse<Message> response) {
                    return Observable.just(response.getResults(), Collections.<Message>emptyList());
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                new Consumer<List<Message>>() {
                    @Override
                    public void accept(List<Message> messages) {
                        onMessagesLoaded(messages);
                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        onRequestFailed();
                    }
                }
            );

        disposables.add(disposable);
    }

    void loadMessagesPaginated() {
        if (isLoadingMessages || allMessagesWereLoaded) return;

        final Disposable disposable = getContactUUid()
            .subscribeOn(Schedulers.io())
            .flatMap(new Function<String, SingleSource<ApiResponse<Message>>>() {
                @Override
                public SingleSource<ApiResponse<Message>> apply(String contactUuid) {
                    return services.loadMessages(contactUuid, nextPageCursor, messagesPageSize);
                }
            })
            .flatMapObservable(new Function<ApiResponse<Message>, ObservableSource<List<Message>>>() {
                @Override
                public ObservableSource<List<Message>> apply(ApiResponse<Message> response) {
                    final String next = response.getNext();

                    if (next != null) {
                        setNextPageCursor(next);
                        return Observable.just(response.getResults());
                    } else {
                        allMessagesWereLoaded = true;
                        return Observable.just(response.getResults(), Collections.<Message>emptyList());
                    }
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(new Consumer<Disposable>() {
                @Override
                public void accept(Disposable disposable) {
                    isLoadingMessages = true;
                }
            })
            .doFinally(new Action() {
                @Override
                public void run() {
                    isLoadingMessages = false;
                }
            })
            .subscribe(
                new Consumer<List<Message>>() {
                    @Override
                    public void accept(List<Message> messages) {
                        onMessagesLoaded(messages);
                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        onRequestFailed();
                    }
                }
            );

        disposables.add(disposable);
    }

    void onRequestFailed() {
        getView().showMessage(R.string.fcm_client_error_load_messages);
    }

    void loadMessage(Bundle data) {
        final Message message = BundleHelper.getMessage(data);
        message.setCreatedOn(new Date());
        getView().onMessageLoaded(message);
    }

    Message createChatMessage(String messageText) {
        final Message chatMessage = new Message();
        setId(chatMessage);

        chatMessage.setText(messageText);
        chatMessage.setCreatedOn(new Date());
        chatMessage.setDirection(Message.DIRECTION_INCOMING);

        return chatMessage;
    }

    void sendMessage(String messageText) {
        getView().addNewMessage(messageText);
        FcmClient.sendMessage(messageText);
    }

    private Single<String> getContactUUid() {
        final String contactUuid = FcmClient.getContact().getUuid();

        if (!TextUtils.isEmpty(contactUuid)) {
            return Single.just(contactUuid);
        }
        final String urn = FcmClient.URN_PREFIX_FCM + FcmClient.getPreferences().getUrn();

        return services.loadContact(urn)
            .map(new Function<ApiResponse<Contact>, String>() {
                @Override
                public String apply(ApiResponse<Contact> response) {
                    return response.getResults().get(0).getUuid();
                }
            });
    }

    private void setNextPageCursor(String nextPageUrl) {
        final String cursorQuery = "cursor=";
        final int startIndex = nextPageUrl.indexOf(cursorQuery) + cursorQuery.length();
        int endIndex = nextPageUrl.indexOf("&", startIndex);
        endIndex = endIndex == -1 ? nextPageUrl.length() : endIndex;

        nextPageCursor = nextPageUrl.substring(startIndex, endIndex);
    }

    private void onMessagesLoaded(List<Message> messages) {
        getView().onMessagesLoaded(messages);
    }

    private void setId(Message chatMessage) {
        final Message lastMessage = getView().getLastMessage();

        if (lastMessage != null) {
            chatMessage.setId(lastMessage.getId() + 1);
        } else {
            chatMessage.setId(0);
        }
    }

}
