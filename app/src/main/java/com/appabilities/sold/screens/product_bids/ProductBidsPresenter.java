package com.appabilities.sold.screens.product_bids;

import com.appabilities.sold.base.BasePresenter;

/**
 * Created by Hamza on 10/23/2017.
 */
public class ProductBidsPresenter extends BasePresenter<ProductBidsContract.View> implements ProductBidsContract.Actions {

    public ProductBidsPresenter(ProductBidsContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }
}