package com.appabilities.sold.networking.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

        import org.parceler.Parcel;

/**
 * Created by Hamza on 10/23/2017.
 */
@Parcel
public class BidListResponse {
    @SerializedName("bidID") @Expose public String bidID;
    @SerializedName("aucted") @Expose public String aucted;
    @SerializedName("userID") @Expose public String userID;
    @SerializedName("bid_price") @Expose public String bidPrice;
    @SerializedName("created_on") @Expose public String createdOn;
    @SerializedName("bid_ordered") @Expose public String bidOrdered;
}
