package com.appabilities.sold.networking.services;

import com.appabilities.sold.networking.response.PaymentResponse;
import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Admin on 10/6/2017.
 */

public interface PaymentService {
    /*@FormUrlEncoded
    @POST("checkouts")
    Call<PaymentResponse> getCheckOutID (
            @Field("authentication.userId") String username,
            @Field("authentication.password") String password,
            @Field("authentication.entityId") String entityID,
            @Field("amount") String amount,
            @Field("currency") String currency,
            @Field("paymentType") String paymentType,
            @Field("merchantTransactionId") String merchantTransactionID,
            @Field("shopperResultUrl") String shopperResultUrl,
            @Field("notificationUrl") String notificationUrl,
            @Field("testMode") String testMode
    );*/


    @FormUrlEncoded
    @POST("v1/checkouts")
    Call<PaymentResponse> getCheckOutID (
            @Field("authentication.userId") String username,
            @Field("authentication.password") String password,
            @Field("authentication.entityId") String entityID,
            @Field("amount") String amount,
            @Field("currency") String currency,
            @Field("paymentType") String paymentType,
            @Field("merchantTransactionId") String merchantTransactionID
           /* @Field("shopperResultUrl") String shopperResultUrl,
            @Field("notificationUrl") String notificationUrl,
            @Field("testMode") String testMode*/);



    /*@GET("paymentStatus.php")
    Call<JsonElement> getPaymentStatus(
            @Query("resourcePath") String resourcePath
    );
*/

    @GET("{resourcePath}")
    Call<JsonElement> getPaymentStatus(@Path(value = "resourcePath") String resourcePath,
                                       @Query("authentication.userId") String userId,
                                       @Query("authentication.password") String password,
                                       @Query("authentication.entityId") String entityId
    );


}
