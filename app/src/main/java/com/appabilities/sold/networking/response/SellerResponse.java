package com.appabilities.sold.networking.response;


import androidx.databinding.BindingAdapter;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Hamza on 11/6/2017.
 */

@Parcel
public class SellerResponse {

    @SerializedName("userID") @Expose public String userID;
    @SerializedName("username") @Expose public String username;
    @SerializedName("email") @Expose public String email;
    @SerializedName("display_name") @Expose public String displayName;
    @SerializedName("description") @Expose public String description;
    @SerializedName("avatar") @Expose public String avatar;
    @SerializedName("cover_image") @Expose public String coverImage;
    @SerializedName("phone") @Expose public String phone;
    @SerializedName("country") @Expose public String country;
    @SerializedName("region") @Expose public String region;
    @SerializedName("address") @Expose public String address;
    @SerializedName("selected_category") @Expose public String selectedCategory;
    @SerializedName("total_following") @Expose public String totalFollowing;
    @SerializedName("total_followers") @Expose public String totalFollowers;
    @SerializedName("is_following") @Expose public String isFollowing;

    @BindingAdapter({"bind:avatar"})
    public static void loadCoverImage(SimpleDraweeView imageView, String imageUrl)
    {
        imageView.setImageURI(imageUrl);
    }

}
