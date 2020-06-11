package com.appabilities.sold.screens.add_detail_advertisement;

import android.util.Patterns;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.PaymentResponse;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.TextValidatiors;

import java.io.File;
import java.net.HttpURLConnection;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 10/26/2017.
 */
public class AddDetailAdvertisementPresenter extends BasePresenter<AddDetailAdvertisementContract.View> implements AddDetailAdvertisementContract.Actions {

    public AddDetailAdvertisementPresenter(AddDetailAdvertisementContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void submitAdvertisement(String accessToken, String advertID,
                                    String advertNoDays, String advertRate, String productID,
                                    String total, File advertImg, String url, String title, String desc) {


        RequestBody body;
        MultipartBody.Builder bodyBuilder;
        bodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("access_token", accessToken)
                .addFormDataPart("advertRID", advertID)
                .addFormDataPart("advert_no_days", advertNoDays)
                .addFormDataPart("advert_rate", advertRate)
                .addFormDataPart("product_id", productID)
                .addFormDataPart("total", total)
                .addFormDataPart("url", url)
                .addFormDataPart("title", title)
                .addFormDataPart("description", desc);

        if (advertID.equals("1")){
            RequestBody request = RequestBody.create(MediaType.parse("application/image"), advertImg);
            bodyBuilder.addFormDataPart("advert_img", advertImg.getName(), request);
        }

        body = bodyBuilder.build();

        Call<BaseResponse> callSubmitAdvert =
                NetController.getInstance().getAdvertisementService().addAdvertisement(body);
        callSubmitAdvert.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.onSuccessfullSubmitAdvert(response.body());
                }else {
                    _view.errorInSubmitAdvert();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                _view.errorInSubmitAdvert();
            }
        });
    }

    @Override
    public void getCheckoutID(String totalAmount, String userID) {
        Call<PaymentResponse> call = NetController.getInstance().getPaymentService().getCheckOutID(
                AppConstants.PAYMENT_KEYS.USER_ID,
                AppConstants.PAYMENT_KEYS.PASSWORD,
                AppConstants.PAYMENT_KEYS.ENTITY_ID,
                totalAmount,
                AppConstants.PAYMENT_KEYS.CURRENCY,
                AppConstants.PAYMENT_KEYS.PAYMENT_TYPE,
                userID
                /*AppConstants.PAYMENT_KEYS_TEST.SHOPPER_RESULT_URL,
                AppConstants.PAYMENT_KEYS_TEST.NOTIFICATION_URL,
                AppConstants.PAYMENT_KEYS_TEST.TEST_MODE*/);

        //AppConstants.PAYMENT_KEYS.TEST_MODE

        call.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.updateCheckoutID(response.body());
                }else {
                    _view.errorInGettingCheckoutID();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                _view.onError(t.getLocalizedMessage());
            }
        });
    }
}