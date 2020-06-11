package com.appabilities.sold.networking.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Hamza on 10/13/2017.
 */

@Parcel
public class OrderResponse {
    @SerializedName("orderID") @Expose public String orderID;
    @SerializedName("order_date") @Expose public String orderDate;
    @SerializedName("userID") @Expose public String userID;
    @SerializedName("shipping_address") @Expose public String shippingAddress;
    @SerializedName("fullname") @Expose public String fullname;
    @SerializedName("email") @Expose public String email;
    @SerializedName("phone") @Expose public String phone;
    @SerializedName("bidID") @Expose public String bidID;
    @SerializedName("total_amt") @Expose public String totalAmt;
    @SerializedName("order_status") @Expose public String orderStatus;
    @SerializedName("order_received") @Expose public String orderReceived;
    @SerializedName("created_on") @Expose public String createdOn;
    @SerializedName("updated_on") @Expose public String updatedOn;
    @SerializedName("order_detail") @Expose public List<OrderResponseDetail> orderDetail = null;
}
