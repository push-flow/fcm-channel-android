package io.fcmchannel.sdk.sample.services;

import android.util.Log;

import io.fcmchannel.sdk.FcmClient;
import io.fcmchannel.sdk.core.models.Contact;
import io.fcmchannel.sdk.core.network.RestServices;
import io.fcmchannel.sdk.services.FcmClientRegistrationIntentService;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by john-mac on 7/2/16.
 */
public class PushRegistrationService extends FcmClientRegistrationIntentService {

    private static final String TAG = "RegistrationService";

    @Override
    public void onFcmRegistered(String pushIdentity, Contact contact) {
        contact.setName("FCM Channel Sample User");
        contact.setEmail("sample@gmail.com");

        final Disposable disposable = FcmClient.getServices()
            .saveContact(contact)
            .subscribe(
                new Consumer<Contact>() {
                    @Override
                    public void accept(Contact contact) { }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e(TAG, "onFcmRegistered: ", throwable);
                    }
                }
            );

        disposables.add(disposable);
    }
}
