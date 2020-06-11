package com.appabilities.sold.screens.change_password.view;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivityChangePasswordBinding;
import com.appabilities.sold.screens.change_password.ChangePasswordContract;
import com.appabilities.sold.screens.change_password.ChangePasswordPresenter;
import com.appabilities.sold.utils.SnackUtils;

public class ChangePasswordActivity extends BaseActivity implements ChangePasswordContract.View {

    ActivityChangePasswordBinding bi;
    ChangePasswordPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        bi.setView(this);
        presenter = new ChangePasswordPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CHANGE PASSWORD");
    }

    @Override
    public void onClickChangePassword() {
        ca.hideKeyboard(this);
        bi.btnChangePassword.startAnimation();
        presenter.changePassword(loginResponse.access_token,
                bi.txtOldPassword.getText().toString().trim(),
                bi.txtNewPassword.getText().toString().trim());
    }

    @Override
    public void errorOldPass(String s) {
        bi.btnChangePassword.revertAnimation();
        bi.txtOldPassword.setError(s);
    }

    @Override
    public void errorNewPass(String s) {
        bi.btnChangePassword.revertAnimation();
        bi.txtNewPassword.setError(s);
    }

    @Override
    public void showError(String msg) {
        bi.btnChangePassword.revertAnimation();
        SnackUtils.showSnackMessage(this, msg);
    }

    @Override
    public void onSuccessfulChangePassword(String msg) {
        bi.btnChangePassword.revertAnimation();
        SnackUtils.showSnackMessage(this, msg);
    }
}
