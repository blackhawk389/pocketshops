package com.appabilities.sold.screens.payment_webview;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.model.OrderModel;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Admin on 1/23/2019.
 */

public class OnlinePaymentPresenter extends BasePresenter<OnlinePaymentContract.View> implements OnlinePaymentContract.Actions {

    protected OnlinePaymentPresenter(OnlinePaymentContract.View view) {
        super(view);
    }

    @Override
    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void placeOrder(OrderModel orderModel) {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(orderModel);
        Call<BaseResponse> call = NetController.getInstance().getOrderService().submitOrder(json);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == HTTP_OK)
                    _view.successfullySubmitOrder();
                else
                    _view.errorSubmitOrder();
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                _view.errorSubmitOrder();
            }
        });
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
                    _view.successfullySubmitAdvertise();
                }else {
                    _view.errorSubmitOrder();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                _view.errorSubmitOrder();
            }
        });
    }
}
