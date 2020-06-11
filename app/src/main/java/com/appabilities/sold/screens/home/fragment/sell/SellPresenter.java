package com.appabilities.sold.screens.home.fragment.sell;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.ProductResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_NOT_AUTHORITATIVE;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 9/15/2017.
 */
public class SellPresenter extends BasePresenter<SellContract.View> implements SellContract.Actions {

    public SellPresenter(SellContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void getMyProductList(String accessToken) {
        _view.onLoading();
        Call<List<ProductResponse>> callMyProductList =
                NetController.getInstance().getProductService().getMyProductsList(accessToken);
        callMyProductList.enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                if (response.code() == HTTP_OK){
                    _view.onRequestSuccessful(response.body());
                }else if (response.code() == HTTP_NOT_FOUND){
                    _view.onRequestNotFound();
                }else {
                    _view.onRequestFail("");
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                _view.onRequestFail("");
            }
        });
    }
}