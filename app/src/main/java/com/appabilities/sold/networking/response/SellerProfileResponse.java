package com.appabilities.sold.networking.response;

import com.appabilities.sold.database.SoldDatabase;
import com.appabilities.sold.model.UserModel;
import com.appabilities.sold.utils.AppConstants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Hamza on 11/6/2017.
 */

@Parcel
public class SellerProfileResponse {
    @SerializedName("userID") @Expose public String userID;
    @SerializedName("username") @Expose public String username;
    @SerializedName("email") @Expose public String email;
    @SerializedName("display_name") @Expose public String displayName;
    @SerializedName("description") @Expose public String description;
    @SerializedName("avatar") @Expose public String avatar;
    @SerializedName("total_following") @Expose public String totalFollowing;
    @SerializedName("total_followers") @Expose public String totalFollowers;
    @SerializedName("product_count") @Expose public String productCount;
    @SerializedName("is_following") @Expose public String isFollowing;
    @SerializedName("cover_image") @Expose public String coverImage;
    @SerializedName("product_detail") @Expose public List<ProductResponse> productDetail = null;


}
