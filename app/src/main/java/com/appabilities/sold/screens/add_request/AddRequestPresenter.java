package com.appabilities.sold.screens.add_request;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.CategoriesResponse;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.File;
import java.net.UnknownHostException;
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
 * Created by Hamza on 10/30/2017.
 */
public class AddRequestPresenter extends BasePresenter<AddRequestContract.View> implements AddRequestContract.Actions {

    public AddRequestPresenter(AddRequestContract.View view) {
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
                    _view.onRequestFail(NetController.getError(response.errorBody()).msg);
                }
            }

            @Override
            public void onFailure(Call<List<CategoriesResponse>> call, Throwable t) {
                if (t instanceof UnknownHostException)
                    _view.onRequestFail("Internet not available");
                else
                    _view.onRequestFail("Unknown error occurred");
            }
        });
    }

    @Override
    public void addRequest(String accessToken, String productTitle, String productDesc, String categoryID,
                           String subCategoryID, String amount, String quantiy, List<Image> imageList, String color, String size) {

        if (productTitle.isEmpty()) {
            _view.showRequestTitleError();
            return;
        } else if (productDesc.isEmpty()) {
            _view.showRequestDescError();
            return;
        } else if (quantiy.isEmpty()) {
            _view.showQuantityError();
            return;
        } else if (imageList.size() == 0) {
            _view.showImageError();
            return;
        }

        _view.onLoading();
        RequestBody productBody;
        MultipartBody.Builder productBuilder;
        productBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                //.addFormDataPart("avatar", avatarfile.getName(), photo)
                .addFormDataPart("product_title", productTitle)
                .addFormDataPart("product_desc", productDesc)
                .addFormDataPart("product_price", amount)
                .addFormDataPart("product_qty", quantiy)
                .addFormDataPart("category_id", categoryID)
                .addFormDataPart("subcategory_id", subCategoryID)
                .addFormDataPart("access_token", accessToken)
                .addFormDataPart("color", color)
                .addFormDataPart("size", size)

                .addFormDataPart("auctionable", "0")
                .addFormDataPart("starting_bid", "0")
                .addFormDataPart("exp_date", "1996/01/23")
                .addFormDataPart("group_id", "0")
                .addFormDataPart("guest_list", "0")
                .addFormDataPart("return_url", "0");

        for (Image image : imageList) {
            File file = new File(image.getPath());
            RequestBody photo = RequestBody.create(MediaType.parse("application/image"), file);
            productBuilder.addFormDataPart("pFiles[]", file.getName(), photo);
        }

        productBody = productBuilder.build();
        Call<BaseResponse> call =
                NetController.getInstance().getRequestService().registerRequestProduct(productBody);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == HTTP_OK) {
                    _view.onSuccessfullyAddedRequest();
                } else {
                    _view.onErrorUploadRequest(NetController.getError(response.errorBody()).msg);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (t instanceof UnknownHostException)
                    _view.onErrorUploadRequest("Internet not available");
                else
                    _view.onErrorUploadRequest("Unknown error occurred");
            }
        });
    }

    @Override
    public void editRequest(String accessToken, String productTitle, String productDesc,
                            String categoryID, String subCategoryID, String amount,
                            String quantiy, List<Image> imageList, String productID,
                            String imagesToDeleteID, String color, String size) {

        if (productTitle.isEmpty()) {
            _view.showRequestTitleError();
            return;
        } else if (productDesc.isEmpty()) {
            _view.showRequestDescError();
            return;
        } else if (quantiy.isEmpty()) {
            _view.showQuantityError();
            return;
        }
        _view.onLoading();

        final RequestBody productBody;
        MultipartBody.Builder productBuilder;
        productBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                //.addFormDataPart("avatar", avatarfile.getName(), photo)
                .addFormDataPart("product_title", productTitle)
                .addFormDataPart("product_desc", productDesc)
                .addFormDataPart("product_price", amount)
                .addFormDataPart("product_qty", quantiy)
                .addFormDataPart("category_id", categoryID)
                .addFormDataPart("subcategory_id", subCategoryID)
                .addFormDataPart("access_token", accessToken)
                .addFormDataPart("product_id", productID)
                .addFormDataPart("imagesToDelete", imagesToDeleteID)
                .addFormDataPart("color", color)
                .addFormDataPart("size", size)

                .addFormDataPart("auctionable", "0")
                .addFormDataPart("starting_bid", "0")
                .addFormDataPart("exp_date", "1996/01/23")
                .addFormDataPart("group_id", "0")
                .addFormDataPart("guest_list", "0")
                .addFormDataPart("return_url", "0");

        for (Image image : imageList) {
            File file = new File(image.getPath());
            RequestBody photo = RequestBody.create(MediaType.parse("application/image"), file);
            productBuilder.addFormDataPart("pFiles[]", file.getName(), photo);
        }

        productBody = productBuilder.build();
        Call<BaseResponse> call = NetController.getInstance().getRequestService().updateRequestProduct(productBody);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == HTTP_OK) {
                    _view.updatedRequestSuccessfully();
                } else {
                    _view.errorRequestUpdate(NetController.getError(response.errorBody()).msg);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (t instanceof UnknownHostException)
                    _view.errorRequestUpdate("Internet not available");
                else
                    _view.errorRequestUpdate("Unknown error occurred");
            }
        });
    }
}