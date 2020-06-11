package com.appabilities.sold.screens.login;

import android.text.TextUtils;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.APIError;
import com.appabilities.sold.networking.ErrorUtils;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.utils.AppConstants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

/**
 * Created by Hamza on 9/13/2017.
 */
public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Actions {

    Call<LoginResponse> callUserLogin;
    public LoginPresenter(LoginContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void onLoginButtonClick(String username, String password) {
        if (TextUtils.isEmpty(username)){
            _view.showUsernameError();
            return;
        }else if (TextUtils.isEmpty(password)){
            _view.showPasswordError();
            return;
        }
        _view.onLoading();
        callUserLogin = NetController.getInstance().getUserService().loginUser(username,
                password,
                AppConstants.regId(_view.getContext()));
        callUserLogin.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.onRequestSuccessful(response.body());
                }else if (response.code() == HTTP_UNAUTHORIZED){
                    _view.onRequestFail("Invalid Username Or Password");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                _view.onRequestFail("Server Error");
            }
        });
    }
}