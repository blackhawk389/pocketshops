package com.appabilities.sold.screens.confirm_order;

import android.util.Log;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.model.OrderModel;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.PaymentResponse;
import com.appabilities.sold.networking.response.VATResponse;
import com.appabilities.sold.utils.AppConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 10/13/2017.
 */
public class ConfirmOrderPresenter extends BasePresenter<ConfirmOrderContract.View> implements ConfirmOrderContract.Actions {

    public ConfirmOrderPresenter(ConfirmOrderContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void callGetSetting() {
        Call<VATResponse> call = NetController.getInstance().getOrderService().getSetting();
        call.enqueue(new Callback<VATResponse>() {
            @Override
            public void onResponse(Call<VATResponse> call, Response<VATResponse> response) {
                if(response.code() == 200){
                    _view.onGetSettingResponse(response.body());
                }
                else {
                    _view.onFailed("Taxes amount not available");
                }
            }

            @Override
            public void onFailure(Call<VATResponse> call, Throwable t) {
                _view.onFailed(t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void placeOrder(OrderModel orderModel) {
        if (orderModel.getFullname().isEmpty()){
            _view.showNameError("Enter Full Name");
            return;
        }else if (orderModel.getEmail().isEmpty()){
            _view.showEmailError("Enter Email");
            return;
        }else if (orderModel.getPhone().isEmpty()){
            _view.showPhoneError("Enter Phone Number");
            return;
        }else if (orderModel.getShipping_address().isEmpty()){
            _view.showAddressError("Enter Address");
            return;
        }

        if (orderModel.getPayment_method().equals("COD")){
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(orderModel);
            Call<BaseResponse> call =
                    NetController.getInstance().getOrderService().submitOrder(json);
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
        }else if (orderModel.getPayment_method().equals("ONLINE")){
            getCheckoutID(orderModel.getTotalAmount(), orderModel.getMerchantTransactionId());
        }

    }

    @Override
    public void getCheckoutID(String totalAmount, String userID) {
        String adjustedAmount = "";
        String[] splitAmount = totalAmount.split(Pattern.quote("."));
        String afterDecimalAmount = splitAmount[1];
        if (afterDecimalAmount != null){
            if (afterDecimalAmount.length() > 0 && afterDecimalAmount.length() == 2){
                adjustedAmount = totalAmount;
            }else if (afterDecimalAmount.length() > 0 && afterDecimalAmount.length() == 1){
                adjustedAmount = splitAmount[0] +"."+splitAmount[1]+"0";
            }else if (afterDecimalAmount.length() > 2){
                adjustedAmount = splitAmount[0] + "."+ splitAmount[1].substring(0,2);
            }else {
                adjustedAmount = splitAmount[0]+".00";
            }
        }else {
            adjustedAmount = splitAmount[0] + ".00";
        }
        Call<PaymentResponse> call= NetController.getInstance().getPaymentService().getCheckOutID(
                AppConstants.PAYMENT_KEYS.USER_ID,
                AppConstants.PAYMENT_KEYS.PASSWORD,
                AppConstants.PAYMENT_KEYS.ENTITY_ID,
                adjustedAmount,
                AppConstants.PAYMENT_KEYS.CURRENCY,
                AppConstants.PAYMENT_KEYS.PAYMENT_TYPE,
                userID
                /*AppConstants.PAYMENT_KEYS_TEST.SHOPPER_RESULT_URL,
                AppConstants.PAYMENT_KEYS_TEST.NOTIFICATION_URL,
                AppConstants.PAYMENT_KEYS_TEST.TEST_MODE*/);

        //AppConstants.PAYMENT_KEYS.TEST_MODE;

        call.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.updateCheckoutID(response.body());
                    Log.d("tag", "onResponse"+ response.body());
                }else {
                    _view.errorInGettingCheckoutID();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                _view.onFailed(t.getLocalizedMessage());
            }
        });
    }
}