package com.appabilities.sold.networking;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oppwa.mobile.connect.exception.PaymentError;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by Admin on 7/25/2017.
 */

public class ErrorUtils {

    public static APIError parseError(Response<?> response) {
        Converter<ResponseBody, APIError> converter =
                NetController.getInstance().getRetrofit().
                        responseBodyConverter(APIError.class, new Annotation[0]);
        APIError error;
        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new APIError();
        }
        return error;
    }


    public static MYPaymentError parsePaymentError(Response<?> response) {
        Gson gson = new GsonBuilder().create();
        MYPaymentError pojo;
        try {
            pojo = gson.fromJson(response.errorBody().string(), MYPaymentError.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new MYPaymentError();
        }
        return pojo;
    }
}
