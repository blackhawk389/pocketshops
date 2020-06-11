package com.appabilities.sold.screens.home;

import com.appabilities.sold.base.BasePresenter;

/**
 * Created by Hamza on 9/15/2017.
 */
public class HomePresenter extends BasePresenter<HomeContract.View> implements HomeContract.Actions {

    public HomePresenter(HomeContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }
}