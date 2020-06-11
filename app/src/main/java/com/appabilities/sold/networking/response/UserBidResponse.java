package com.appabilities.sold.networking.response;


import androidx.databinding.BindingAdapter;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Hamza on 10/20/2017.
 */
@Parcel
public class UserBidResponse {
    @SerializedName("productID") @Expose public String productID;
    @SerializedName("product_title") @Expose public String productTitle;
    @SerializedName("product_desc") @Expose public String productDesc;
    @SerializedName("price") @Expose public String price;
    @SerializedName("quantity") @Expose public String quantity;
    @SerializedName("starting_bid") @Expose public String startingBid;
    @SerializedName("product_owner") @Expose public String productOwner;
    @SerializedName("owner_userID") @Expose public String ownerUserID;
    @SerializedName("img_name") @Expose public String imgName;
    @SerializedName("categoryID") @Expose public String categoryID;
    @SerializedName("subcategoryID") @Expose public String subcategoryID;
    @SerializedName("category_name") @Expose public String categoryName;
    @SerializedName("sub_category_name") @Expose public String subCategoryName;
    @SerializedName("groupID") @Expose public String groupID;
    @SerializedName("bid_list") @Expose public List<BidListResponse> bidList = null;

    @BindingAdapter({"bind:imgName"})
    public static void loadCoverImage(SimpleDraweeView imageView, String imageUrl)
    {
        imageView.setImageURI(imageUrl);
    }
}
