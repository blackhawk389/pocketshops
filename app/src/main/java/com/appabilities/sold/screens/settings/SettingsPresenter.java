package com.appabilities.sold.screens.settings;

import com.appabilities.sold.base.BasePresenter;

/**
 * Created by Hamza on 10/19/2017.
 */
public class SettingsPresenter extends BasePresenter<SettingsContract.View> implements SettingsContract.Actions {

    public SettingsPresenter(SettingsContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }
}