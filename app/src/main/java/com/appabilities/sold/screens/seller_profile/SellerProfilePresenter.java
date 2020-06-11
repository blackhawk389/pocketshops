package com.appabilities.sold.screens.seller_profile;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.SellerProfileResponse;
import com.appabilities.sold.networking.response.VerifyUserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 10/27/2017.
 */
public class SellerProfilePresenter extends BasePresenter<SellerProfileContract.View> implements SellerProfileContract.Actions {

    public SellerProfilePresenter(SellerProfileContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void getProfile(String accessToken, String sellerID) {
        _view.onLoading();
        Call<SellerProfileResponse> call = NetController.getInstance().getUserService().getSellerProfile(
                accessToken, sellerID
        );
        call.enqueue(new Callback<SellerProfileResponse>() {
            @Override
            public void onResponse(Call<SellerProfileResponse> call, Response<SellerProfileResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.updateProfile(response.body());
                }else {
                    _view.errorInGettingProfile();
                }
            }

            @Override
            public void onFailure(Call<SellerProfileResponse> call, Throwable t) {
                _view.errorInGettingProfile();
            }
        });
    }

    @Override
    public void updateFollowStatus(String accessToken, String followType, String followUserID, String followStatus) {
        Call<VerifyUserResponse> call = NetController.getInstance().getUserService()
                .followUser(accessToken, followType, followUserID, followStatus);
        call.enqueue(new Callback<VerifyUserResponse>() {
            @Override
            public void onResponse(Call<VerifyUserResponse> call, Response<VerifyUserResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.updateFollowStatus(response.body());
                }else {
                    _view.errorFollowStatus();
                }
            }

            @Override
            public void onFailure(Call<VerifyUserResponse> call, Throwable t) {

            }
        });
    }
}