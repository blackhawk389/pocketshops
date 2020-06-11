package com.appabilities.sold.screens.my_product_detail;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 10/3/2017.
 */
public class MyProductDetailPresenter extends BasePresenter<MyProductDetailContract.View> implements MyProductDetailContract.Actions {

    public MyProductDetailPresenter(MyProductDetailContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void deleteProduct(String accessToken, String productID) {
        Call<BaseResponse> call = NetController.getInstance().getProductService().deleteProduct(
                accessToken, productID
        );

        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.productDeletedSuccessfully(response.body());
                }else {
                    _view.errorInDeletingProduct();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                _view.errorInDeletingProduct();
            }
        });
    }
}