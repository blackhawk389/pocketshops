package com.appabilities.sold.networking.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Admin on 7/11/2018.
 */
@Parcel
public class SellerDetail {
    @SerializedName("username")
    @Expose
    public String username;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("full_name")
    @Expose
    public String fullName;
    @SerializedName("phone")
    @Expose
    public String phone;
}
