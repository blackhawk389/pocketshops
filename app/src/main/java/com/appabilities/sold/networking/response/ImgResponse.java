package com.appabilities.sold.networking.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Hamza on 9/25/2017.
 */

@Parcel
public class ImgResponse {
    @SerializedName("imageID") @Expose public String imageID;
    @SerializedName("img_name") @Expose public String imgName;
}
