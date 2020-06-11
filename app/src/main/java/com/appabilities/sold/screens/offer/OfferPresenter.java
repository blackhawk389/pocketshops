package com.appabilities.sold.screens.offer;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.OfferResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 11/1/2017.
 */
public class OfferPresenter extends BasePresenter<OfferContract.View> implements OfferContract.Actions {

    public OfferPresenter(OfferContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void getOffers(String accessToken, String productID) {
        Call<List<OfferResponse>> call = NetController.getInstance().getRequestService().getOfferResponse(
                accessToken, productID
        );
        call.enqueue(new Callback<List<OfferResponse>>() {
            @Override
            public void onResponse(Call<List<OfferResponse>> call, Response<List<OfferResponse>> response) {
                if (response.code() == HTTP_OK) {
                    _view.onRequestSuccessful(response.body());
                } else if (response.code() == HTTP_NOT_FOUND) {
                    _view.onRequestNotFound();
                } else {
                    _view.onRequestFail("");
                }
            }

            @Override
            public void onFailure(Call<List<OfferResponse>> call, Throwable t) {
                _view.onRequestFail("");
            }
        });
    }

    @Override
    public void acceptOffer(String accessToken, final OfferResponse offerResponse) {
        Call<BaseResponse> call = NetController.getInstance().getRequestService().acceptOffer(
                accessToken, offerResponse.productID, offerResponse.offerID);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.offerAcceptedSuccessfully(offerResponse);
                }else {
                    _view.errorInAcceptingOffer();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                _view.errorInAcceptingOffer();
            }
        });
    }
}