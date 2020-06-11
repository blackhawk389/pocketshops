package com.appabilities.sold.screens.my_order_detail;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.VATResponse;
import com.appabilities.sold.utils.AppConstants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hamza on 10/16/2017.
 */
public class MyOrderDetailPresenter extends BasePresenter<MyOrderDetailContract.View> implements MyOrderDetailContract.Actions {

    public MyOrderDetailPresenter(MyOrderDetailContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }


}