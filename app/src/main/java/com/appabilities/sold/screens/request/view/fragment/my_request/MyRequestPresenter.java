package com.appabilities.sold.screens.request.view.fragment.my_request;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.RequestResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 10/30/2017.
 */
public class MyRequestPresenter extends BasePresenter<MyRequestContract.View> implements MyRequestContract.Actions {

    public MyRequestPresenter(MyRequestContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void getRequest(String accessToken) {
        _view.onLoading();
        Call<List<RequestResponse>> call = NetController.getInstance().getRequestService().getRequestResponse(
                accessToken, "1");
        call.enqueue(new Callback<List<RequestResponse>>() {
            @Override
            public void onResponse(Call<List<RequestResponse>> call, Response<List<RequestResponse>> response) {
                if (response.code() == HTTP_OK) {
                    _view.onRequestSuccessful(response.body());
                } else if (response.code() == HTTP_NOT_FOUND) {
                    _view.onRequestNotFound();
                } else {
                    _view.onRequestFail(NetController.getError(response.errorBody()).msg);
                }
            }

            @Override
            public void onFailure(Call<List<RequestResponse>> call, Throwable t) {
                _view.onRequestFail("");
            }
        });
    }
}