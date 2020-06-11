package com.appabilities.sold.screens.auction;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.UserBidResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 10/20/2017.
 */
public class MyAuctionPresenter extends BasePresenter<MyAuctionContract.View> implements MyAuctionContract.Actions {

    public MyAuctionPresenter(MyAuctionContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void getBidList(String accessToken) {
        _view.onLoading();
        Call<List<UserBidResponse>> call = NetController.getInstance().getProductService().getUserBidsList(accessToken);
        call.enqueue(new Callback<List<UserBidResponse>>() {
            @Override
            public void onResponse(Call<List<UserBidResponse>> call, Response<List<UserBidResponse>> response) {
                if (response.code() == HTTP_OK){
                    _view.onRequestSuccessful(response.body());
                }else if (response.code() == HTTP_NOT_FOUND){
                    _view.onRequestNotFound();
                }else {
                    _view.onRequestFail("");
                }
            }

            @Override
            public void onFailure(Call<List<UserBidResponse>> call, Throwable t) {
                _view.onRequestFail("");
            }
        });
    }
}