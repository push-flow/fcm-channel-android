package io.fcmchannel.sdk.core.network;

import io.fcmchannel.sdk.core.models.Contact;
import io.fcmchannel.sdk.core.models.Message;
import io.fcmchannel.sdk.core.models.network.ApiResponse;
import io.fcmchannel.sdk.core.models.network.FcmRegistrationResponse;
import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by gualberto on 6/13/16.
 */
public interface RapidProApi {

    @FormUrlEncoded
    @POST("c/fcm/{channel}/register")
    Single<FcmRegistrationResponse> registerFcmContact(
            @Path("channel") String channel,
            @Field("urn") String urn,
            @Field("fcm_token") String fcmToken,
            @Field("contact_uuid") String contactUuid
    );

    @FormUrlEncoded
    @POST("c/fcm/{channel}/receive")
    Completable sendReceivedMessage(
            @Path("channel") String channel,
            @Field("from") String from,
            @Field("fcm_token") String fcmToken,
            @Field("msg") String msg
    );

    @GET("api/v2/messages.json")
    Single<ApiResponse<Message>> listMessages(
            @Header("Authorization") String token,
            @Query("contact") String contactUuid
    );

    @GET("api/v2/messages.json")
    Single<ApiResponse<Message>> listMessages(
            @Header("Authorization") String token,
            @Query("contact") String contactUuid,
            @Query("cursor") String cursor,
            @Query("count") int count
    );

    @GET("api/v2/contacts.json")
    Single<ApiResponse<Contact>> loadContact(
            @Header("Authorization") String token,
            @Query("urn") String urn
    );

    @POST("api/v2/contacts.json")
    Single<Contact> saveContact(
            @Header("Authorization") String token,
            @Query("uuid") String contactUuid,
            @Body Contact contact
    );

}
