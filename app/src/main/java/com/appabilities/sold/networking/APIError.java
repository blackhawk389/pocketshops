package com.appabilities.sold.networking;

/**
 * Created by Admin on 7/25/2017.
 */

public class APIError {

    private int statusCode;
    private String message;

    public APIError() {

    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
