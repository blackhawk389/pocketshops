package com.appabilities.sold.networking.services;

import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.OfferResponse;
import com.appabilities.sold.networking.response.RequestResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by Hamza on 10/30/2017.
 */

public interface RequestService {

    @GET("api/getRequestProductList")
    Call<List<RequestResponse>> getRequestResponse(
            @Query("access_token") String accessToken,
            @Query("request_type") String requestType);

    @POST("api/addRequestProduct")
    Call<BaseResponse> registerRequestProduct(
            @Body RequestBody productBody
    );

    @POST("api/addOffer")
    Call<BaseResponse> addOfferBody(
            @Body RequestBody productBody
    );

    @GET("api/getOfferDetail")
    Call<List<OfferResponse>> getOfferResponse(
            @Query("access_token") String access_token,
            @Query("product_id") String productID);

    @GET("api/accept_offer")
    Call<BaseResponse> acceptOffer(
            @Query("access_token") String accessToken,
            @Query("product_id") String productID,
            @Query("offer_id") String offerID
    );

    @POST("api/updateRequestProduct")
    Call<BaseResponse> updateRequestProduct(
            @Body RequestBody productBody
    );

    @FormUrlEncoded
    @PUT("api/deleteRequest")
    Call<BaseResponse> deleteRequest(@Field("access_token") String access_token, @Field("product_id") String product_id);

    @GET("api/getOfferDetail")
    Call<List<OfferResponse>> getRequestOffers(
            @Query("product_id") String productID
    );


    @GET("api/addWithDrawRequest")
    Call<BaseResponse> getWithdrawlResponse(
            @Query("access_token") String accessToken,
            @Query("req_amount") float req_amount);


}
