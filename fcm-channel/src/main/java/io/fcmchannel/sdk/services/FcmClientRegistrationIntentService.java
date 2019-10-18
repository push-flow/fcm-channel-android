package io.fcmchannel.sdk.services;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.util.Pair;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Collections;

import io.fcmchannel.sdk.FcmClient;
import io.fcmchannel.sdk.core.models.Contact;
import io.fcmchannel.sdk.core.models.network.FcmRegistrationResponse;
import io.fcmchannel.sdk.core.network.RestServices;
import io.fcmchannel.sdk.persistence.Preferences;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by john-mac on 6/27/16.
 */
public class FcmClientRegistrationIntentService extends IntentService {

    private static final String TAG = "RegistrationIntent";

    public static final String ACTION_REGISTRATION_COMPLETE = "io.fcmchannel.sdk.RegistrationCompleted";
    public static final String EXTRA_URN = "urn";
    public static final String EXTRA_CONTACT_UUID = "contactUuid";

    public FcmClientRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final String urn = intent.getStringExtra(EXTRA_URN);
        final String contactUuid = intent.getStringExtra(EXTRA_CONTACT_UUID);

        registerContact(urn, contactUuid)
            .doFinally(new Action() {
                @Override
                public void run() {
                    Intent registrationComplete = new Intent(ACTION_REGISTRATION_COMPLETE);
                    LocalBroadcastManager
                        .getInstance(FcmClientRegistrationIntentService.this)
                        .sendBroadcast(registrationComplete);
                }
            })
            .subscribe(
                new Consumer<Pair<String, Contact>>() {
                    @Override
                    public void accept(Pair<String, Contact> pair) {
                        onFcmRegistered(pair.first, pair.second);
                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e(TAG, "onHandleIntent: ", throwable);
                    }
                }
            );
    }

    protected void onFcmRegistered(String pushIdentity, Contact contact) { }

    private Single<Pair<String, Contact>> registerContact(final String urn, final String contactUuid) {
        final String fcmToken = FirebaseInstanceId.getInstance().getToken();

        if (TextUtils.isEmpty(FcmClient.getToken())) {
            return Single.just(Pair.create(fcmToken, (Contact) null));
        }
        return new RestServices(FcmClient.getHost(), FcmClient.getToken())
            .registerFcmContact(
                FcmClient.getChannel(),
                urn,
                fcmToken,
                contactUuid
            )
            .doOnSuccess(new Consumer<FcmRegistrationResponse>() {
                @Override
                public void accept(FcmRegistrationResponse response) {
                    final Preferences preferences = FcmClient.getPreferences();
                    preferences.setContactUuid(response.getContactUuid());
                    preferences.setFcmToken(fcmToken);
                    preferences.setUrn(urn);
                    preferences.commit();
                }
            })
            .map(new Function<FcmRegistrationResponse, Contact>() {
                @Override
                public Contact apply(FcmRegistrationResponse response) {
                    final Contact contact = new Contact();
                    contact.setUuid(response.getContactUuid());
                    contact.setUrns(Collections.singletonList(FcmClient.URN_PREFIX_FCM + urn));
                    return contact;
                }
            })
            .map(new Function<Contact, Pair<String, Contact>>() {
                @Override
                public Pair<String, Contact> apply(Contact contact) {
                    return Pair.create(fcmToken, contact);
                }
            });
    }

}
