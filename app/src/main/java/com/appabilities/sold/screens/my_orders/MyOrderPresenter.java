package com.appabilities.sold.screens.my_orders;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.OrderResponse;

import java.net.HttpURLConnection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

/**
 * Created by Hamza on 10/13/2017.
 */
public class MyOrderPresenter extends BasePresenter<MyOrderContract.View> implements MyOrderContract.Actions {

    public MyOrderPresenter(MyOrderContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void getOrders(String accessToken) {
        _view.onLoading();
        Call<List<OrderResponse>> call =
                NetController.getInstance().getOrderService().getMyOrders(accessToken);
        call.enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                if (response.code() == HttpURLConnection.HTTP_OK){
                    _view.onRequestSuccessful(response.body());
                }else if (response.code() == HTTP_NOT_FOUND){
                    _view.onRequestNotFound();
                }else {
                    _view.onRequestFail("");
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                _view.onRequestFail("");
            }
        });
    }
}