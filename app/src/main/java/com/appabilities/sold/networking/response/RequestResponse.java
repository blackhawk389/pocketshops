package com.appabilities.sold.networking.response;


import androidx.databinding.BindingAdapter;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Hamza on 10/30/2017.
 */

@Parcel
public class RequestResponse {
    @SerializedName("productID") @Expose public String productID;
    @SerializedName("product_title") @Expose public String productTitle;
    @SerializedName("product_desc") @Expose public String productDesc;
    @SerializedName("price") @Expose public String price;
    @SerializedName("quantity") @Expose public String quantity;
    @SerializedName("starting_bid") @Expose public String startingBid;
    @SerializedName("product_owner") @Expose public String productOwner;
    @SerializedName("userID") @Expose public String userID;
    @SerializedName("ratings") @Expose public String ratings;
    @SerializedName("reviews") @Expose public String reviews;
    @SerializedName("highest_bid") @Expose public String highestBid;
    @SerializedName("img_name") @Expose public String imgName;
    @SerializedName("updated_on") @Expose public String updatedOn;
    @SerializedName("category_name") @Expose public String categoryName;
    @SerializedName("subcategory_name") @Expose public String subcategoryName;
    @SerializedName("auctionable") @Expose public String auctionable;
    @SerializedName("requester") @Expose public String requester;
    @SerializedName("offer_count") @Expose public String offerCount;
    @SerializedName("requester_name") @Expose public String requesterName;
    @SerializedName("requester_avatar") @Expose public String requesterAvatar;
    @SerializedName("color") @Expose public String color;
    @SerializedName("size") @Expose public String size;
    @SerializedName("requester_id") @Expose public String requesterID;
    @SerializedName("img_names") @Expose public List<ImgResponse> imgNames = null;

    @BindingAdapter({"bind:imgName"})
    public static void loadCoverImage(SimpleDraweeView imageView, String imageUrl)
    {
        imageView.setImageURI(imageUrl);
    }

    @BindingAdapter({"bind:requester_avatar"})
    public static void loadRequesterImage(SimpleDraweeView imageView, String imageUrl)
    {
        imageView.setImageURI(imageUrl);
    }
}
