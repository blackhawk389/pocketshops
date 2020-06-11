package com.appabilities.sold.networking.services;

import com.appabilities.sold.networking.response.CategoriesResponse;
import com.appabilities.sold.networking.response.VerifyUserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PUT;

/**
 * Created by Hamza on 9/15/2017.
 */

public interface CategoriesService {
    @GET("api/getCategories")
    Call<List<CategoriesResponse>> getCategoriesList();
    @FormUrlEncoded
    @PUT("api/addSelectedCategory")
    Call<VerifyUserResponse> addCategorySelections(@Field("access_token") String access_token,
                                                   @Field("categories") String categories);
}
