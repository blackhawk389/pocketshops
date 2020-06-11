package com.appabilities.sold.screens.change_password;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 10/19/2017.
 */
public class ChangePasswordPresenter extends BasePresenter<ChangePasswordContract.View> implements ChangePasswordContract.Actions {

    public ChangePasswordPresenter(ChangePasswordContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void changePassword(String accessToken,String oldPass, String newPass) {
        if (oldPass.isEmpty()){
            _view.errorOldPass("Input Old Password");
            return;
        }else if (newPass.isEmpty()){
            _view.errorNewPass("Input New Password");
            return;
        }
        Call<BaseResponse> call =
                NetController.getInstance().getUserService().changePassword(oldPass, newPass, accessToken);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.onSuccessfulChangePassword(response.body().msg);
                }else {
                    Converter<ResponseBody, BaseResponse> converter = NetController.getInstance().getRetrofit().responseBodyConverter(BaseResponse.class, new Annotation[0]);
                    BaseResponse error = null;
                    try {
                        error = converter.convert(response.errorBody());
                        _view.showError(error.msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

            }
        });
    }
}