package com.appabilities.sold.networking.response;

import com.google.gson.annotations.Expose;

/**
 * Created by Hamza on 9/14/2017.
 */

public class VerifyUserResponse {
    @Expose public boolean status;
    @Expose public String msg;
    @Expose public int user_status;
    @Expose public int email_status;
    @Expose public String record;
}
