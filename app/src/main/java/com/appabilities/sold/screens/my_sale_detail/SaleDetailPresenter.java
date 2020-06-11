package com.appabilities.sold.screens.my_sale_detail;

import com.appabilities.sold.base.BasePresenter;

/**
 * Created by Hamza on 10/17/2017.
 */
public class SaleDetailPresenter extends BasePresenter<SaleDetailContract.View> implements SaleDetailContract.Actions {

    public SaleDetailPresenter(SaleDetailContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }
}