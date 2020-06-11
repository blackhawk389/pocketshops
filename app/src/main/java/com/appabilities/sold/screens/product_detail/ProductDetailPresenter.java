package com.appabilities.sold.screens.product_detail;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.VerifyUserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 10/2/2017.
 */
public class ProductDetailPresenter extends BasePresenter<ProductDetailContract.View> implements ProductDetailContract.Actions {

    public ProductDetailPresenter(ProductDetailContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
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