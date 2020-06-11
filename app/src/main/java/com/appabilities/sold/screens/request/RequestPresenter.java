package com.appabilities.sold.screens.request;

import com.appabilities.sold.base.BasePresenter;

/**
 * Created by Hamza on 10/30/2017.
 */
public class RequestPresenter extends BasePresenter<RequestContract.View> implements RequestContract.Actions {

    public RequestPresenter(RequestContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }
}