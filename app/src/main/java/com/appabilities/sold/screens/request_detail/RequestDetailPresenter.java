package com.appabilities.sold.screens.request_detail;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 10/31/2017.
 */
public class RequestDetailPresenter extends BasePresenter<RequestDetailContract.View> implements RequestDetailContract.Actions {

    public RequestDetailPresenter(RequestDetailContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void deleteRequest(String accessToken, String productID) {
        Call<BaseResponse> call = NetController.getInstance().getRequestService().deleteRequest(
                accessToken, productID
        );

        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.requestDeletedSuccessfully(response.body());
                }else {
                    _view.errorInDeletingRequest();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                _view.errorInDeletingRequest();
            }
        });
    }
}