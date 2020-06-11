package com.appabilities.sold.screens.seller_profile.fragment.product_grid;

import com.appabilities.sold.base.BasePresenter;

/**
 * Created by Hamza on 11/3/2017.
 */
public class ProductGridPresenter extends BasePresenter<ProductGridContract.View> implements ProductGridContract.Actions {

    public ProductGridPresenter(ProductGridContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }
}