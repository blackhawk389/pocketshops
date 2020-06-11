package com.appabilities.sold.networking.services;

import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.BidItemResponse;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.networking.response.SaleResponse;
import com.appabilities.sold.networking.response.UserBidResponse;
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
 * Created by Hamza on 9/25/2017.
 */

public interface ProductService {
    @GET("api/getRecommendedProducts")
    Call<List<ProductResponse>> getTopProductList(@Query("offset") String offset, @Query("access_token") String accessToken);

    @GET("api/getLatestProducts")
    Call<List<ProductResponse>> getRecentProductList(@Query("offset") String offset, @Query("access_token") String accessToken);

    @FormUrlEncoded
    @PUT("api/addUpdateProductLikes")
    Call<VerifyUserResponse> likeProduct(
            @Field("access_token") String access_token,
            @Field("product_id") String product_id,
            @Field("like_status") int like_status
    );

    @GET("api/getProductList")
    Call<List<ProductResponse>> getMyProductsList(@Query("access_token") String access_token);

    @GET("api/getFavoriteProductList")
    Call<List<ProductResponse>> getFavouriteProductsList(
            @Query("access_token") String access_token);

    @GET("api/getProducts")
    Call<List<ProductResponse>> getProductList(@Query("search_txt") String searchTxt,
                                               @Query("price_min") String priceMin,
                                               @Query("price_max") String priceMax,
                                               @Query("sort_by") String sortBy,
                                               @Query("category_id") String categoryID,
                                               @Query("subcategory_id") String subCategoryID,
                                               @Query("seller") String sellerName,
                                               @Query("access_token") String accessToken);

    @POST("api/addProduct")
    Call<BaseResponse> registerProductBody(
            @Body RequestBody productBody
    );

    @POST("api/updateProduct")
    Call<ProductResponse> updateProductBody(
            @Body RequestBody productBody
    );

    @FormUrlEncoded
    @POST("api/addReviews")
    Call<BaseResponse> submitReview(
            @Field("access_token") String accessToken,
            @Field("product_id") String productID,
            @Field("rating") String rating,
            @Field("order_did") String orderDID,
            @Field("comments") String comments
    );

    @GET("api/getMySales")
    Call<List<SaleResponse>> getSalesList(
            @Query("access_token") String access_token
    );

    @FormUrlEncoded
    @POST("api/addProductBid")
    Call<BaseResponse> placeBid(
            @Field("access_token") String access_token,
            @Field("product_id") String product_id,
            @Field("bid_price") String bid_price
    );

    @GET("api/getUserBidList")
    Call<List<UserBidResponse>> getUserBidsList(
            @Query("access_token") String access_token
    );

    @GET("api/getBidList")
    Call<List<BidItemResponse>> getBidsList(
            @Query("access_token") String access_token,
            @Query("product_id") String productID
    );

    @FormUrlEncoded
    @PUT("api/awardBid")
    Call<BaseResponse> awardBid(
            @Field("bid_id") String bid_id,
            @Field("access_token") String access_token
    );

    @GET("api/getProductList")
    Call<List<ProductResponse>> getSellerProductsList(@Query("user_id") String userid);

    @FormUrlEncoded
    @PUT("api/deleteProduct")
    Call<BaseResponse> deleteProduct(@Field("access_token") String access_token, @Field("product_id") String product_id);

}
