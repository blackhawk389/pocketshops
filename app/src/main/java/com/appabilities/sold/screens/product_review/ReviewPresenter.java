package com.appabilities.sold.screens.product_review;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 10/16/2017.
 */
public class ReviewPresenter extends BasePresenter<ReviewContract.View> implements ReviewContract.Actions {

    public ReviewPresenter(ReviewContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void submitReview(String accessToken, String productID,
                             String rating, String orderDID, String comment) {

        Call<BaseResponse> call = NetController.getInstance().getProductService().submitReview(
                accessToken,
                productID,
                rating,
                orderDID,
                comment
        );
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.showSuccessfullReviewSubmit();
                }else {
                    _view.showErrorSubmitReview();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                _view.showErrorSubmitReview();
            }
        });
    }
}