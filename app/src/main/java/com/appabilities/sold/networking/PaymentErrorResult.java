package com.appabilities.sold.networking;

/**
 * Created by Admin on 1/2/2019.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentErrorResult {

    @SerializedName("code") @Expose public String code;

    @SerializedName("description") @Expose public String description;

    @SerializedName("timestamp") @Expose public String timestamp;

}
