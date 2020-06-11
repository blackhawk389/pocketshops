package com.appabilities.sold.networking.services;

import com.appabilities.sold.model.GetFollowers;
import com.appabilities.sold.model.RevenueModel;
import com.appabilities.sold.model.UserModel;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.networking.response.RegisterUserResponse;
import com.appabilities.sold.networking.response.SellerProfileResponse;
import com.appabilities.sold.networking.response.SellerResponse;
import com.appabilities.sold.networking.response.VerifyUserResponse;

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
 * Created by Hamza on 9/13/2017.
 */

public interface UserService {
    @FormUrlEncoded
    @POST("api/login")
    Call<LoginResponse> loginUser (
            @Field("username") String username,
            @Field("password") String password,
            @Field("device_id") String deviceID
    );

    @FormUrlEncoded
    @POST("api/verifyUser")
    Call<VerifyUserResponse> verifyUser(
            @Field("username") String username,
            @Field("email") String email
    );

    @POST("api/register")
    Call<RegisterUserResponse> registerUserBody(
            @Body RequestBody photo
    );


    @FormUrlEncoded
    @POST("api/changePassword")
    Call<BaseResponse> changePassword (
            @Field("old_password") String oldPassword,
            @Field("new_password") String newPassword,
            @Field("access_token") String accessToken
    );



    @GET("api/getSellerProfile")
    Call<GetFollowers> getFollowers(
            @Query("access_token") String accessToken);


    @GET("api/getSellers")
    Call<List<SellerResponse>> getSellers(
            @Query("access_token") String accessToken,
            @Query("offset") String offset,
            @Query("search_user") String text
    );

    @GET("api/getSellerInfo")
    Call<SellerProfileResponse> getSellerProfile(
            @Query("access_token") String accessToken,
            @Query("seller_id") String sellerID
    );

    @GET("api/getMyRevenue")
    Call<RevenueModel> getUserRevenue(
            @Query("date") String date,
            @Query("access_token") String accessToken
    );

    @FormUrlEncoded
    @POST("api/addUpdateFollowers")
    Call<VerifyUserResponse> followUser (
            @Field("access_token") String access_token,
            @Field("follow_type") String follow_type,
            @Field("follow_userid") String follow_userid,
            @Field("follow_status") String follow_status
    );

    @FormUrlEncoded
    @POST("api/forgetPassword")
    Call<BaseResponse> forgetPassword (
            @Field("email") String access_token
    );

    @POST("api/updateProfileCover")
    Call<LoginResponse> updateUserCoverPhoto(
            @Body RequestBody body
    );

    @POST("api/profile")
    Call<LoginResponse> updateUserBody(
            @Body RequestBody updateRequest
    );

    @POST("api/sendChatFCM")
    Call<BaseResponse> sendChatFCM(
            @Body RequestBody updateRequest
    );
}
