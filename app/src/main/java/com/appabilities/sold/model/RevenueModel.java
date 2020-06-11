package com.appabilities.sold.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hamza on 1/12/2018.
 */

public class RevenueModel {
    @SerializedName("total_earning") @Expose public String totalEarning;
    @SerializedName("total_commission") @Expose public String totalCommission;
    @SerializedName("deducted_commission") @Expose public String deductedCommission;
    @SerializedName("due_commission") @Expose public String dueCommission;
    @SerializedName("available_amount") @Expose public String availableAmount;
    @SerializedName("non_available_amount") @Expose public String nonAvailableAmount;
    @SerializedName("requested_amount") @Expose public String requested_amount;
    @SerializedName("received_amount") @Expose public String received_amount;
}
