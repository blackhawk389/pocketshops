package com.appabilities.sold.screens.user_profile;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.model.GetFollowers;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.networking.response.SellerProfileResponse;
import com.appabilities.sold.networking.response.SellerResponse;
import com.appabilities.sold.utils.SnackUtils;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 11/3/2017.
 */
public class UserProfilePresenter extends BasePresenter<UserProfileContract.View> implements UserProfileContract.Actions {

    public UserProfilePresenter(UserProfileContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void getFollowers(String accessToken) {
        Call<GetFollowers> call = NetController.getInstance().getUserService().getFollowers(
                accessToken
        );
        call.enqueue(new Callback<GetFollowers>() {
            @Override
            public void onResponse(Call<GetFollowers> call, Response<GetFollowers> response) {
                if (response.code() == HTTP_OK){
                    _view.onGetFollowers(response.body());
                }else {
                   _view.oGetFollowersError();
                }
            }

            @Override
            public void onFailure(Call<GetFollowers> call, Throwable t) {
                _view.oGetFollowersError();
            }
        });
    }

    @Override
    public void postCover(String accessToken, File imgFile) {
        RequestBody body;
        MultipartBody.Builder bodyBuilder;
        bodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("access_token", accessToken);


        RequestBody request = RequestBody.create(MediaType.parse("application/image"), imgFile);
        bodyBuilder.addFormDataPart("cover_image", imgFile.getName(), request);

        body = bodyBuilder.build();

        Call<LoginResponse> call = NetController.getInstance()
                .getUserService().updateUserCoverPhoto(body);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.successfullyUpdateCover(response.body());
                }else {
                    _view.errorUpdateCover(NetController.getError(response.errorBody()).msg);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });
    }
}