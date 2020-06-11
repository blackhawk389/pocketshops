package com.appabilities.sold.screens.my_account;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.model.RevenueModel;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 1/11/2018.
 */
public class MyAccountPresenter extends BasePresenter<MyAccountContract.View> implements MyAccountContract.Actions {

    public MyAccountPresenter(MyAccountContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void getRevenues(String date, String accessToken) {
        Call<RevenueModel> call = NetController.getInstance().getUserService().getUserRevenue(
                date, accessToken);
        call.enqueue(new Callback<RevenueModel>() {
            @Override
            public void onResponse(Call<RevenueModel> call, Response<RevenueModel> response) {
                if (response.code() == HTTP_OK){
                    _view.onSuccess(response.body());
                }else {
                    _view.onFailure();
                }
            }

            @Override
            public void onFailure(Call<RevenueModel> call, Throwable t) {
                _view.onFailure();
            }
        });
    }

    @Override
    public void requestWithdrawl(String accessToken, float amount) {
        Call<BaseResponse> call = NetController.getInstance().getRequestService().getWithdrawlResponse(accessToken, amount);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.onSuccessWithdrawl(response.body());
                }else {
                    _view.onFailureWithdrawl();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                _view.onFailureWithdrawl();
            }
        });
    }
}