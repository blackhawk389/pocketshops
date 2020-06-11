package com.appabilities.sold.networking.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Hamza on 10/16/2017.
 */

@Parcel
public class OrderResponseDetail {
    @SerializedName("orderDID") @Expose public String orderDID;
    @SerializedName("orderID") @Expose public String orderID;
    @SerializedName("productID") @Expose public String productID;
    @SerializedName("product_title") @Expose public String productTitle;
    @SerializedName("product_url") @Expose public String productUrl;
    @SerializedName("total_price") @Expose public String totalPrice;
    @SerializedName("qty") @Expose public String qty;
    @SerializedName("userID") @Expose public String userID;
    @SerializedName("product_amt") @Expose public String productAmt;
    @SerializedName("shipping") @Expose public String shipping;
    @SerializedName("taxes") @Expose public String taxes;
    @SerializedName("order_status") @Expose public String orderStatus;
    @SerializedName("order_received") @Expose public String orderReceived;
    @SerializedName("order_date") @Expose public String orderDate;
    @SerializedName("reviewed") @Expose public String isReviewed;
    @SerializedName("updated_on") @Expose public String updatedOn;
    @SerializedName("seller_detail") @Expose public SellerDetail sellerDetail;
}
