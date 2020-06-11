package com.appabilities.sold.base.sidemenu;


import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.response.LoginResponse;

/**
 * Created by Wajahat Karim on 4/23/2017.
 */
public class SideMenuBasePresenter extends BasePresenter<SideMenuBaseContract.View> implements SideMenuBaseContract.Actions {

    protected SideMenuBasePresenter(SideMenuBaseContract.View view) {
        super(view);
    }

    @Override
    public void updateHeaderData(LoginResponse loginResponse) {
        _view.updateHeader(loginResponse.display_name, loginResponse.email, loginResponse.avatar);
    }
}
