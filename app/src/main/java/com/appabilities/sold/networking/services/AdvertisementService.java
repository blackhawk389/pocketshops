package com.appabilities.sold.networking.services;

import com.appabilities.sold.networking.response.AdCategoryResponse;
import com.appabilities.sold.networking.response.AdvertisementResponse;
import com.appabilities.sold.networking.response.BaseResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Hamza on 9/25/2017.
 */

public interface AdvertisementService {

    @GET("api/getAdvertRate")
    Call<List<AdCategoryResponse>> getAdvertCategory();

    @GET("api/getAdvert")
    Call<List<AdvertisementResponse>> getAdvertisement(
            @Query("type") String type,
            @Query("access_token") String accessToken
    );

    @POST("api/addAdvert")
    Call<BaseResponse> addAdvertisement (
            @Body RequestBody body
    );
}
