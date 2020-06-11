package com.appabilities.sold.screens.categories;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.CategoriesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 10/2/2017.
 */
public class CategoriesPresenter extends BasePresenter<CategoriesContract.View> implements CategoriesContract.Actions {

    public CategoriesPresenter(CategoriesContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void getCategories() {
        _view.onLoading();
        Call<List<CategoriesResponse>> call = NetController.getInstance().getCategoriesService().getCategoriesList();
        call.enqueue(new Callback<List<CategoriesResponse>>() {
            @Override
            public void onResponse(Call<List<CategoriesResponse>> call, Response<List<CategoriesResponse>> response) {
                if (response.code() == HTTP_OK) {
                    _view.onRequestSuccessful(response.body());
                } else if (response.code() == HTTP_NOT_FOUND) {
                    _view.onRequestNotFound();
                } else {
                    _view.onRequestFail("");
                }
            }

            @Override
            public void onFailure(Call<List<CategoriesResponse>> call, Throwable t) {
                _view.onRequestFail("");
            }
        });
    }
}