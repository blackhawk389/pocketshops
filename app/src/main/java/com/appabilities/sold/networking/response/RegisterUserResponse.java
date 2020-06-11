package com.appabilities.sold.networking.response;

import com.google.gson.annotations.Expose;

/**
 * Created by Hamza on 9/14/2017.
 */

public class RegisterUserResponse {
    @Expose public String status;
    @Expose public String msg;
    @Expose public String access_token;
    @Expose public String userID;
    @Expose public String avatar;
}
