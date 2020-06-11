package com.appabilities.sold.networking.response;


import androidx.databinding.BindingAdapter;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hamza on 9/25/2017.
 */

public class AdvertisementResponse {
    @SerializedName("advert_img") @Expose public String advertImg;
    @SerializedName("productID") @Expose public String productID;
    @SerializedName("advert_type") @Expose public String advertType;
    @SerializedName("active") @Expose public String active;
    @SerializedName("advertID") @Expose public String advertID;
    @SerializedName("product_tile") @Expose public String productTitle;
    @SerializedName("product_image") @Expose public String productImg;
    @SerializedName("state") @Expose public String state;
    @SerializedName("product_desc") @Expose public String productDesc;
    @SerializedName("ad_amount") @Expose public String adAmount;
    @SerializedName("url") @Expose public String url;
    @SerializedName("title") @Expose public String title;
    @SerializedName("description") @Expose public String description;

    @BindingAdapter({"bind:advertImg"})
    public static void loadCoverImage(SimpleDraweeView imageView, String imageUrl)
    {
        imageView.setImageURI(imageUrl);
    }
}
