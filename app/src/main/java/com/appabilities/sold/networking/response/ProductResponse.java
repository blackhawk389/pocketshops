package com.appabilities.sold.networking.response;


import androidx.databinding.BindingAdapter;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Hamza on 9/25/2017.
 */
@Parcel
public class ProductResponse {
    @SerializedName("productID") @Expose public String productID;
    @SerializedName("product_title") @Expose public String productTitle;
    @SerializedName("product_desc") @Expose public String productDesc;
    @SerializedName("price") @Expose public String price;
    @SerializedName("quantity") @Expose public String quantity;
    @SerializedName("liked") @Expose public String liked;
    @SerializedName("product_owner") @Expose public String productOwner;
    @SerializedName("product_owner_description") @Expose public String productOwnerDescription;
    @SerializedName("product_owner_avatar") @Expose public String productOwnerAvatar;
    @SerializedName("product_owner_cover_image") @Expose public String productOwnerCoverImage;
    @SerializedName("userID") @Expose public String userID;
    @SerializedName("ratings") @Expose public String ratings;
    @SerializedName("highest_bid") @Expose public String highestBid;
    @SerializedName("reviews") @Expose public String reviews;
    @SerializedName("img_name") @Expose public String imgName;
    @SerializedName("updated_on") @Expose public String updatedOn;
    @SerializedName("categoryID") @Expose public String categoryID;
    @SerializedName("subcategoryID") @Expose public String subcategoryID;
    @SerializedName("groupID") @Expose public String groupID;
    @SerializedName("auctionable") @Expose public String auctionable;
    @SerializedName("shipping_cost") @Expose public String shippingCost;
    @SerializedName("taxes") @Expose public String taxes;
    @SerializedName("starting_bid") @Expose public String startingBid;
    @SerializedName("auction_exp_date") @Expose public String auctionExpDate;
    @SerializedName("color") @Expose public String color;
    @SerializedName("size") @Expose public String size;
    @SerializedName("sponsered") @Expose public String sponsered;
    @SerializedName("aucted") @Expose public Integer aucted;
    @SerializedName("aucted_userid") @Expose public Integer auctedUserid;
    @SerializedName("img_names") @Expose public List<ImgResponse> imgNames = null;

    public boolean isSelected = true;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @BindingAdapter({"bind:imgName"})
    public static void loadCoverImage(SimpleDraweeView imageView, String imageUrl)
    {
        imageView.setImageURI(imageUrl);
    }

    @BindingAdapter({"bind:productOwnerCoverImage"})
    public static void loadProductOwnerImage(SimpleDraweeView imageView, String imageUrl)
    {
        imageView.setImageURI(imageUrl);
    }

}
