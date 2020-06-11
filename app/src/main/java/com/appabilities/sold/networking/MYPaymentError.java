package com.appabilities.sold.networking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 1/2/2019.
 */

public class MYPaymentError {

    @SerializedName("result") @Expose public PaymentErrorResult result;

    @SerializedName("buildNumber") @Expose public String buildNumber;

    @SerializedName("ndc") @Expose public String ndc;

}
