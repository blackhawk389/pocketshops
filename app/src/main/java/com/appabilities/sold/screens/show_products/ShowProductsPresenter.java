package com.appabilities.sold.screens.show_products;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.networking.response.VerifyUserResponse;

import java.net.UnknownHostException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 10/4/2017.
 */
public class ShowProductsPresenter extends BasePresenter<ShowProductsContract.View> implements ShowProductsContract.Actions {

    public ShowProductsPresenter(ShowProductsContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void getProducts(String searchText, String minPrice, String maxPrice,
                            String sortBy, String categoryID, String subCategoryID, String sellerName, String accessToken) {
        _view.onLoading();
        Call<List<ProductResponse>> callProductList =
                NetController.getInstance().getProductService().getProductList(searchText,
                        minPrice, maxPrice, sortBy, categoryID, subCategoryID, sellerName, accessToken);
        callProductList.enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                if (response.code() == HTTP_OK) {
                    _view.onRequestSuccessful(response.body());
                } else if (response.code() == HTTP_NOT_FOUND) {
                    _view.onRequestNotFound();
                } else {
                    _view.onRequestFail(NetController.getError(response.errorBody()).msg);
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                if (t instanceof UnknownHostException)
                    _view.onRequestFail("Internet not available");
                else
                    _view.onRequestFail("Unknown error occurred");
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
                if (response.code() == HTTP_OK) {
                    _view.updateProductLikeStatus(response.body(), status);
                } else {
                    _view.errorUpdateLikeStatus(NetController.getError(response.errorBody()).msg);
                }
            }

            @Override
            public void onFailure(Call<VerifyUserResponse> call, Throwable t) {
                if (t instanceof UnknownHostException)
                    _view.errorUpdateLikeStatus("Internet not available");
                else
                    _view.errorUpdateLikeStatus("Unknown error occurred");
            }
        });
    }
}