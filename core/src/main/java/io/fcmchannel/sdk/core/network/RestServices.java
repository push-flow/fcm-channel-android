package io.fcmchannel.sdk.core.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.fcmchannel.sdk.core.adapters.GsonDateTypeAdapter;
import io.fcmchannel.sdk.core.adapters.HashMapTypeAdapter;
import io.fcmchannel.sdk.core.models.Message;
import io.fcmchannel.sdk.core.models.network.ApiResponse;
import io.fcmchannel.sdk.core.models.network.FcmRegistrationResponse;
import io.fcmchannel.sdk.core.models.Contact;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
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

    public RestServices(String host, String token) {
        this.token = token;
        this.host = host;

        checkFields(token);
        buildApi();
    }

    private void checkFields(String token) {
        if (this.token != null && !token.startsWith("Token")) {
            this.token = String.format(TOKEN_FORMAT, token);
        }
    }

    private void buildApi() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .writeTimeout(TIMEOUT_IN_SECONDS, TimeUnit.MINUTES)
                .connectTimeout(TIMEOUT_IN_SECONDS, TimeUnit.MINUTES)
                .readTimeout(TIMEOUT_IN_SECONDS, TimeUnit.MINUTES)
                .addInterceptor(logging)
                .build();

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new GsonDateTypeAdapter())
                .registerTypeAdapter(HashMap.class, new HashMapTypeAdapter())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .client(client)
                .addConverterFactory (GsonConverterFactory.create(gson))
                .build();

        rapidProApi = retrofit.create(RapidProApi.class);
    }

    public Call<ApiResponse<Contact>> loadContact(String urn) {
        return rapidProApi.loadContact(token, urn);
    }

    public Call<FcmRegistrationResponse> registerFcmContact(String channel, String urn, String fcmToken,
                                                            String contactUuid) {
        return rapidProApi.registerFcmContact(channel, urn, fcmToken, contactUuid);
    }

    public Call<ResponseBody> sendReceivedMessage(String channel, String from, String fcmToken, String msg) {
        return rapidProApi.sendReceivedMessage(channel, from, fcmToken, msg);
    }

    public Call<Contact> saveContact(Contact contact) {
        return rapidProApi.saveContact(token, contact.getUuid(), contact);
    }

    public Call<ApiResponse<Message>> loadMessages(String contactUuid) {
        return rapidProApi.listMessages(token, contactUuid);
    }

    public Call<ApiResponse<Message>> loadMessages(String contactUuid, String cursor, int count) {
        return rapidProApi.listMessages(token, contactUuid, cursor, count);
    }

}
