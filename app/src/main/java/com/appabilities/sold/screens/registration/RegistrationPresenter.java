package com.appabilities.sold.screens.registration;

import android.text.TextUtils;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.VerifyUserResponse;
import com.appabilities.sold.utils.TextValidatiors;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 9/14/2017.
 */
public class RegistrationPresenter extends BasePresenter<RegistrationContract.View> implements RegistrationContract.Actions {

    Call<VerifyUserResponse> callVerifyUser;
    public RegistrationPresenter(RegistrationContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void verifyUser(String username, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(username)){
            _view.showUsernameError("Enter Username");
            return;
        }else if (TextUtils.isEmpty(email)){
            _view.showEmailError("Enter Email Address");
            return;
        }else if (!TextValidatiors.isValidEmail(email)){
            _view.showEmailError("Enter Valid Email Address");
            return;
        }else if (TextUtils.isEmpty(password)){
            _view.showPasswordError("Enter Password");
            return;
        }else if (TextUtils.isEmpty(confirmPassword)){
            _view.showConfirmPassError("Enter Confirm Password");
            return;
        }else if (!password.equals(confirmPassword)){
            _view.showConfirmPassError("Password Not Matched");
            return;
        }
        _view.onLoading();
        callVerifyUser = NetController.getInstance().getUserService().verifyUser(username, email);
        callVerifyUser.enqueue(new Callback<VerifyUserResponse>() {
            @Override
            public void onResponse(Call<VerifyUserResponse> call, Response<VerifyUserResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.onRequestSuccessful(response.body());
                }else {
                    Converter<ResponseBody, VerifyUserResponse> converter = NetController.getInstance().getRetrofit().responseBodyConverter(VerifyUserResponse.class, new Annotation[0]);
                    try {
                        VerifyUserResponse error = converter.convert(response.errorBody());

                        if (error.email_status == 1)
                        {
                            _view.showEmailError("Email Already Exists!");
                        }
                        if (error.user_status == 1)
                        {
                            _view.showUsernameError("Username Already Exists!");
                        }
                        _view.onRequestFail(error.msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                        _view.onRequestFail(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<VerifyUserResponse> call, Throwable t) {
                _view.onRequestFail(t.getMessage());
            }
        });

    }
}