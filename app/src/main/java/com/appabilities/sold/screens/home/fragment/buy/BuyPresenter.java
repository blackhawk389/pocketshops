package com.appabilities.sold.screens.home.fragment.buy;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.AdvertisementResponse;
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
public class BuyPresenter extends BasePresenter<BuyContract.View> implements BuyContract.Actions {

    public BuyPresenter(BuyContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void getAdvertisement() {
        Call<List<AdvertisementResponse>> callAdvertisement =
                NetController.getInstance().getAdvertisementService().getAdvertisement("Public","");
        callAdvertisement.enqueue(new Callback<List<AdvertisementResponse>>() {
            @Override
            public void onResponse(Call<List<AdvertisementResponse>> call, Response<List<AdvertisementResponse>> response) {
                if (response.code() == HTTP_OK)
                    _view.updateAdvertisement(response.body(), false);
                else
                    _view.updateAdvertisement(response.body(), true);
                    //_view.advertisementError();
            }

            @Override
            public void onFailure(Call<List<AdvertisementResponse>> call, Throwable t) {
                _view.advertisementError();
            }
        });
    }

    @Override
    public void getTopProducts(String accessToken) {
        Call<List<ProductResponse>> callTopProducts =
                NetController.getInstance().getProductService().getTopProductList("0", accessToken);
        callTopProducts.enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                if (response.code() == HTTP_OK){
                    _view.updateTopProducts(response.body());
                }else if (response.code() == HTTP_NOT_FOUND){
                    _view.showEmptyTopProducts();
                }else {
                    _view.showErrorTopProducts();
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                _view.showErrorTopProducts();
            }
        });
    }

    @Override
    public void getRecentProducts(String accessToken) {
        Call<List<ProductResponse>> callRecentProducts =
                NetController.getInstance().getProductService().getRecentProductList("0", accessToken);
        callRecentProducts.enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                if (response.code() == HTTP_OK){
                    _view.updateRecentProducts(response.body());
                }else if (response.code() == HTTP_NOT_FOUND){
                    _view.showEmptyRecentProducts();
                }else {
                    _view.showErrorRecentProducts();
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                _view.showErrorRecentProducts();
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