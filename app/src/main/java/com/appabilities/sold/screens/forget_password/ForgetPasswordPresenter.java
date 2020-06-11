package com.appabilities.sold.screens.forget_password;

import android.os.Handler;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.utils.TextValidatiors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;


/**
 * Created by Hamza on 7/19/2017.
 */
public class ForgetPasswordPresenter extends BasePresenter<ForgetPasswordContract.View> implements
        ForgetPasswordContract.Actions {

    public ForgetPasswordPresenter(ForgetPasswordContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void resetPassword(String email) {

        if (email.isEmpty()){
            _view.showEmailError("Input Email To Change Password");
            return;
        }else if (!TextValidatiors.isValidEmail(email)){
            _view.showEmailError("Input Valid Email Address");
            return;
        }
        _view.showProgress();

        Call<BaseResponse> call = NetController.getInstance().getUserService().forgetPassword(email);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.showSuccess(response.body().msg);
                }else {
                    _view.showFailure(NetController.getError(response.errorBody()).msg);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                _view.showFailure("");
            }
        });

    }

}