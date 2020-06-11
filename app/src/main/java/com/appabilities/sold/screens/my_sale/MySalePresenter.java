package com.appabilities.sold.screens.my_sale;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.SaleResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 10/17/2017.
 */
public class MySalePresenter extends BasePresenter<MySaleContract.View> implements MySaleContract.Actions {

    public MySalePresenter(MySaleContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void getSales(String accessToken) {
        Call<List<SaleResponse>> call = NetController.getInstance().getProductService().getSalesList(accessToken);
        call.enqueue(new Callback<List<SaleResponse>>() {
            @Override
            public void onResponse(Call<List<SaleResponse>> call, Response<List<SaleResponse>> response) {
                if (response.code() == HTTP_OK){
                    _view.onRequestSuccessful(response.body());
                }else if (response.code() == HTTP_NOT_FOUND){
                    _view.onRequestNotFound();
                }else {
                    _view.onRequestFail(NetController.getError(response.errorBody()).msg);
                }
            }

            @Override
            public void onFailure(Call<List<SaleResponse>> call, Throwable t) {
                _view.onRequestFail("");
            }
        });
    }
}