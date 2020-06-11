package com.appabilities.sold.screens.advertisement;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.AdvertisementResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 10/25/2017.
 */
public class AdvertisementPresenter extends BasePresenter<AdvertisementContract.View> implements AdvertisementContract.Actions {

    public AdvertisementPresenter(AdvertisementContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void getAdvertisement(String accessToken) {
        _view.onLoading();
        Call<List<AdvertisementResponse>> call = NetController.getInstance().getAdvertisementService().getAdvertisement(
                "",accessToken);
        call.enqueue(new Callback<List<AdvertisementResponse>>() {
            @Override
            public void onResponse(Call<List<AdvertisementResponse>> call, Response<List<AdvertisementResponse>> response) {
                if (response.code() == HTTP_OK){
                    _view.onRequestSuccessful(response.body());
                }else if (response.code() == HTTP_NOT_FOUND){
                    _view.onRequestNotFound();
                }else {
                    _view.onRequestSuccessful("");
                }
            }

            @Override
            public void onFailure(Call<List<AdvertisementResponse>> call, Throwable t) {
                _view.onRequestFail("");
            }
        });
    }
}