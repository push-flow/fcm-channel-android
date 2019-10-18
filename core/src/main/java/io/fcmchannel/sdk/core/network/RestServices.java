package io.fcmchannel.sdk.core.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.fcmchannel.sdk.core.adapters.GsonDateTypeAdapter;
import io.fcmchannel.sdk.core.adapters.HashMapTypeAdapter;
import io.fcmchannel.sdk.core.models.Contact;
import io.fcmchannel.sdk.core.models.Message;
import io.fcmchannel.sdk.core.models.network.ApiResponse;
import io.fcmchannel.sdk.core.models.network.FcmRegistrationResponse;
import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gualberto on 6/13/16.
 */
public class RestServices {

    private static final String TOKEN_FORMAT = "Token %s";
    private static final int TIMEOUT_IN_SECONDS = 1;

    private String token;
    private String host;
    private RapidProApi rapidProApi;

    public RestServices(final String host, final String token) {
        this.token = token;
        this.host = host;

        checkFields(token);
        buildApi();
    }

    private void checkFields(final String token) {
        if (this.token != null && !token.startsWith("Token")) {
            this.token = String.format(TOKEN_FORMAT, token);
        }
    }

    private void buildApi() {
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);

        final OkHttpClient client = new OkHttpClient.Builder()
                .writeTimeout(TIMEOUT_IN_SECONDS, TimeUnit.MINUTES)
                .connectTimeout(TIMEOUT_IN_SECONDS, TimeUnit.MINUTES)
                .readTimeout(TIMEOUT_IN_SECONDS, TimeUnit.MINUTES)
                .addInterceptor(logging)
                .build();

        final Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new GsonDateTypeAdapter())
                .registerTypeAdapter(HashMap.class, new HashMapTypeAdapter())
                .create();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        rapidProApi = retrofit.create(RapidProApi.class);
    }

    public Single<ApiResponse<Contact>> loadContact(final String urn) {
        return rapidProApi.loadContact(token, urn);
    }

    public Single<FcmRegistrationResponse> registerFcmContact(
            final String channel,
            final String urn,
            final String fcmToken,
            final String contactUuid
    ) {
        return rapidProApi.registerFcmContact(channel, urn, fcmToken, contactUuid);
    }

    public Completable sendReceivedMessage(
            final String channel,
            final String from,
            final String fcmToken,
            final String msg
    ) {
        return rapidProApi.sendReceivedMessage(channel, from, fcmToken, msg);
    }

    public Single<Contact> saveContact(final Contact contact) {
        return rapidProApi.saveContact(token, contact.getUuid(), contact);
    }

    public Single<ApiResponse<Message>> loadMessages(final String contactUuid) {
        return rapidProApi.listMessages(token, contactUuid);
    }

    public Single<ApiResponse<Message>> loadMessages(
            final String contactUuid,
            final String cursor,
            final int count
    ) {
        return rapidProApi.listMessages(token, contactUuid, cursor, count);
    }

}
