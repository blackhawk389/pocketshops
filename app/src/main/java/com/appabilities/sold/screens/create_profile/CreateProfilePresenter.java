package com.appabilities.sold.screens.create_profile;

import android.util.Log;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.APIError;
import com.appabilities.sold.networking.ErrorUtils;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.networking.response.RegisterUserResponse;
import com.appabilities.sold.utils.AppConstants;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 9/14/2017.
 */
public class CreateProfilePresenter extends BasePresenter<CreateProfileContract.View> implements CreateProfileContract.Actions {

    public CreateProfilePresenter(CreateProfileContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void registerUser(String username, String email, String password, String displayName, String address, String phoneNo, String country, String region, String description, File avatarFile) {
        _view.onLoading();
        RequestBody body;
        MultipartBody.Builder bodyBuilder;
        bodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", username)
                .addFormDataPart("email", email)
                .addFormDataPart("password", password)
                .addFormDataPart("display_name", displayName)
                .addFormDataPart("address", address)
                .addFormDataPart("phone", phoneNo)
                .addFormDataPart("country", country)
                .addFormDataPart("region", region)
                .addFormDataPart("description", description)
                .addFormDataPart("device_id", AppConstants.regId(_view.getContext()));

        if (avatarFile != null)
        {
            RequestBody photo = RequestBody.create(MediaType.parse("application/image"), avatarFile);
            bodyBuilder.addFormDataPart("avatar", avatarFile.getName(), photo);
        }

        body = bodyBuilder.build();
        Call<RegisterUserResponse> call = NetController.getInstance().getUserService().registerUserBody(body);
        call.enqueue(new Callback<RegisterUserResponse>() {
            @Override
            public void onResponse(Call<RegisterUserResponse> call, Response<RegisterUserResponse> response) {
                if (response.code() == HTTP_OK)
                {
                    _view.onRequestSuccessful(response.body());
                }
                else
                {
                    APIError error = ErrorUtils.parseError(response);
                    _view.onRequestFail(error.getMessage());
                }
            }

            @Override
            public void onFailure(Call<RegisterUserResponse> call, Throwable t) {
                _view.onRequestFail(t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void updateUser(String accessToken, String displayName, String address, String phoneNo, String country, String region, String description, File avatarFile) {
        _view.onLoading();
        RequestBody updateRequest;
        MultipartBody.Builder requestBuilder;
        requestBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                //.addFormDataPart("avatar", avatarfile.getName(), photo)
                .addFormDataPart("access_token", accessToken)
                .addFormDataPart("display_name", displayName)
                .addFormDataPart("address", address)
                .addFormDataPart("phone", phoneNo)
                .addFormDataPart("country", country)
                .addFormDataPart("region", region)
                .addFormDataPart("description", description);

        if (avatarFile != null)
        {
            // Photo Parameter
            RequestBody photo = RequestBody.create(MediaType.parse("application/image"), avatarFile);
            requestBuilder.addFormDataPart("avatar", avatarFile.getName(), photo);
        }

        updateRequest = requestBuilder.build();

        Call<LoginResponse> call = NetController.getInstance().getUserService().updateUserBody(updateRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.onUpdateSuccessful(response.body());
                }else {
                    _view.onRequestFail(NetController.getError(response.errorBody()).msg);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                _view.onRequestFail(t.getLocalizedMessage());
            }
        });
    }
}