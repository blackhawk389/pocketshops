package com.appabilities.sold.networking.services;

import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.OrderResponse;
import com.appabilities.sold.networking.response.VATResponse;
import com.google.gson.JsonElement;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Hamza on 10/13/2017.
 */

public interface OrderService {
    @FormUrlEncoded
    @POST("api/submitOrder")
    Call<BaseResponse> submitOrder(
            @Field("orders") String order
    );

    @GET("api/getMyOrders")
    Call<List<OrderResponse>> getMyOrders(@Query("access_token") String access_token);

    @FormUrlEncoded
    @POST("api/recievedOrder")
    Call<BaseResponse> updateOrderStatus(
            @Field("access_token") String accessToken,
            @Field("orderDID") String orderID,
            @Field("order_status") String orderStatus
    );
    @GET("{resourcePath}")
    Call<JsonElement> getPaymentStatus(@Path(value = "resourcePath", encoded = true) String resourcePath,
                                       @Query("authentication.userId") String userId,
                                       @Query("authentication.password") String password,
                                       @Query("authentication.entityId") String entityId
    );

    @GET("api/getSetting")
    Call<VATResponse> getSetting();
}