package com.appabilities.sold.networking.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Hamza on 10/26/2017.
 */

@Parcel
public class AdCategoryResponse {
    @SerializedName("advertRID") @Expose public String advertRID;
    @SerializedName("advert_type") @Expose public String advertType;
    @SerializedName("rate") @Expose public String rate;
}
