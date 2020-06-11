package com.appabilities.sold.networking.response;

import com.google.gson.annotations.Expose;

import org.parceler.Parcel;

/**
 * Created by Hamza on 10/23/2017.
 */

@Parcel
public class BidItemResponse {
    @Expose public String bidID;
    @Expose public String bid_price;
    @Expose public String bidder_id;
    @Expose public String display_name;
    @Expose public String avatar;
    @Expose public String region;
    @Expose public String country;
    @Expose public String approved;
}
