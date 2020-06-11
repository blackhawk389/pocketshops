package com.appabilities.sold.networking.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 3/6/2019.
 */

public class VATResponse {

    @SerializedName("vat") @Expose public String vat;
    @SerializedName("commission") @Expose public String commission;
}
