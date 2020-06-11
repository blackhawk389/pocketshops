package com.appabilities.sold.networking.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hamza on 10/27/2017.
 */

public class PaymentResponse {
    @SerializedName("paymentErrorResult") @Expose public ResultResponse result;
    @SerializedName("buildNumber") @Expose public String buildNumber;
    @SerializedName("timestamp") @Expose public String timestamp;
    @SerializedName("ndc") @Expose public String ndc;
    @SerializedName("id") @Expose public String id;

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "paymentErrorResult=" + result +
                ", buildNumber='" + buildNumber + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", ndc='" + ndc + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
