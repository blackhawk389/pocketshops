package com.appabilities.sold.networking.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Hamza on 11/1/2017.
 */
@Parcel
public class OfferResponse {
    @SerializedName("offerID") @Expose public String offerID;
    @SerializedName("productID") @Expose public String productID;
    @SerializedName("userID") @Expose public String userID;
    @SerializedName("title") @Expose public String title;
    @SerializedName("detail") @Expose public String detail;
    @SerializedName("price") @Expose public String price;
    @SerializedName("quantity") @Expose public String quantity;
    @SerializedName("status") @Expose public String status;
    @SerializedName("created_on") @Expose public String createdOn;
    @SerializedName("seller_name") @Expose public String sellerName;
    @SerializedName("category_name") @Expose public String categoryName;
    @SerializedName("subcategory_name") @Expose public String subCategoryName;
    @SerializedName("img_names") @Expose public List<ImgResponse> imgNames = null;
    @SerializedName("seller_avatar") @Expose public String seller_avatar;
}
