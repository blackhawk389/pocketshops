package com.appabilities.sold.screens.auction_bid;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BidItemResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 10/23/2017.
 */
public class AuctionBidPresenter extends BasePresenter<AuctionBidContract.View> implements AuctionBidContract.Actions {

    public AuctionBidPresenter(AuctionBidContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void getBidList(String accessToken, String productID) {
        _view.onLoading();
        Call<List<BidItemResponse>> call =
                NetController.getInstance().getProductService().getBidsList(accessToken, productID);
        call.enqueue(new Callback<List<BidItemResponse>>() {
            @Override
            public void onResponse(Call<List<BidItemResponse>> call, Response<List<BidItemResponse>> response) {
                if (response.code() == HTTP_OK) {
                    _view.onRequestSuccessful(response.body());
                } else if (response.code() == HTTP_NOT_FOUND) {
                    _view.onRequestNotFound();
                } else {
                    _view.onRequestFail("");
                }
            }

            @Override
            public void onFailure(Call<List<BidItemResponse>> call, Throwable t) {
                _view.onRequestFail("");
            }
        });
    }
}