package com.appabilities.sold.networking.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hamza on 10/27/2017.
 */

public class ResultResponse {
    @SerializedName("code") @Expose
    public String code;
    @SerializedName("description") @Expose public String description;

    @Override
    public String toString() {
        return "ResultResponse{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
