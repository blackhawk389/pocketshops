package com.appabilities.sold.screens.search_seller;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.SellerResponse;
import com.appabilities.sold.networking.response.VerifyUserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 11/6/2017.
 */
public class SearchSellerPresenter extends BasePresenter<SearchSellerContract.View> implements SearchSellerContract.Actions {

    public SearchSellerPresenter(SearchSellerContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void getSellers(String accessToken, String offset, String keyword) {
        Call<List<SellerResponse>> call = NetController.getInstance().getUserService().getSellers(
                accessToken, offset, keyword);
        call.enqueue(new Callback<List<SellerResponse>>() {
            @Override
            public void onResponse(Call<List<SellerResponse>> call, Response<List<SellerResponse>> response) {
                if (response.code() == HTTP_OK){
                    _view.onRequestSuccessful(response.body());
                }else if (response.code() == HTTP_NOT_FOUND){
                    _view.onRequestNotFound();
                }else {
                    _view.onRequestFail("");
                }
            }

            @Override
            public void onFailure(Call<List<SellerResponse>> call, Throwable t) {
                _view.onRequestFail("");
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
                if (response.code() == HTTP_OK) {
                    _view.updateFollowStatus(response.body());
                } else {
                    _view.errorFollowStatus();
                }
            }

            @Override
            public void onFailure(Call<VerifyUserResponse> call, Throwable t) {

            }
        });
    }
}