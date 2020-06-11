package com.appabilities.sold.screens.place_bid;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 10/19/2017.
 */
public class PlaceBidPresenter extends BasePresenter<PlaceBidContract.View> implements PlaceBidContract.Actions {

    public PlaceBidPresenter(PlaceBidContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void placeBid(String accessToken, String productID, String bidPrice) {
        Call<BaseResponse> call = NetController.getInstance().getProductService().placeBid(
                accessToken, productID, bidPrice
        );

        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.successfullyPlaceBid("Bid Placed Successfully!");
                }else {
                    _view.errorInPlaceBid("Error");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                _view.errorInPlaceBid("Error");
            }
        });
    }
}