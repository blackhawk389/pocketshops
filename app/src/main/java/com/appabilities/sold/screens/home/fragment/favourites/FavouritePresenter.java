package com.appabilities.sold.screens.home.fragment.favourites;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.networking.response.VerifyUserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 9/15/2017.
 */
public class FavouritePresenter extends BasePresenter<FavouriteContract.View> implements FavouriteContract.Actions {

    public FavouritePresenter(FavouriteContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void getUserFavouriteProduct(String accessToken) {
        _view.onLoading();
        Call<List<ProductResponse>> callUserFavProducts =
                NetController.getInstance().getProductService().getFavouriteProductsList(accessToken);
        callUserFavProducts.enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                if (response.code() == HTTP_OK){
                    _view.onRequestSuccessful(response.body());
                }else if (response.code() == HTTP_NOT_FOUND){
                    _view.onRequestNotFound();
                }else {
                    _view.onRequestFail("");
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                _view.onRequestFail("");
            }
        });
    }

    @Override
    public void onUpdateProductLike(String accessToken, String productID, final int status) {
        Call<VerifyUserResponse> callUpdateLikeProduct =
                NetController.getInstance().getProductService().likeProduct(accessToken, productID, status);
        callUpdateLikeProduct.enqueue(new Callback<VerifyUserResponse>() {
            @Override
            public void onResponse(Call<VerifyUserResponse> call, Response<VerifyUserResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.updateProductLikeStatus(response.body(), status);
                }else{
                    _view.errorUpdateLikeStatus();
                }
            }

            @Override
            public void onFailure(Call<VerifyUserResponse> call, Throwable t) {
                _view.errorUpdateLikeStatus();
            }
        });
    }
}