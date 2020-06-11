package com.appabilities.sold.screens.sub_category;

import com.appabilities.sold.base.BasePresenter;

/**
 * Created by Hamza on 10/4/2017.
 */
public class SubCategoryPresenter extends BasePresenter<SubCategoryContract.View> implements SubCategoryContract.Actions {

    public SubCategoryPresenter(SubCategoryContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }
}