package com.appabilities.sold.screens.seller_profile.fragment.product_list;

import com.appabilities.sold.base.BasePresenter;

/**
 * Created by Hamza on 11/3/2017.
 */
public class ProductListPresenter extends BasePresenter<ProductListContract.View> implements ProductListContract.Actions {

    public ProductListPresenter(ProductListContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }
}