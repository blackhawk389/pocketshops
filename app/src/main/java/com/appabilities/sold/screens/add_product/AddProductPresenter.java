package com.appabilities.sold.screens.add_product;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.CategoriesResponse;
import com.appabilities.sold.networking.response.ProductResponse;
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
 * Created by Hamza on 10/10/2017.
 */
public class AddProductPresenter extends BasePresenter<AddProductContract.View> implements AddProductContract.Actions {

    public AddProductPresenter(AddProductContract.View view) {
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
    public void addProduct(String accessToken, String productTitle, String productDesc, String categoryID,
                           String subCategoryID, String amount, String quantiy, List<Image> imageList, String color, String size) {

        if (productTitle.isEmpty()) {
            _view.showProductTitleError();
            return;
        } else if (productDesc.isEmpty()) {
            _view.showProductDescError();
            return;
        } else if (amount.isEmpty()) {
            _view.showAmountError();
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
                .addFormDataPart("color", color)
                .addFormDataPart("size", size)
                .addFormDataPart("access_token", accessToken);

        for (Image image : imageList) {
            File file = new File(image.getPath());
            RequestBody photo = RequestBody.create(MediaType.parse("application/image"), file);
            productBuilder.addFormDataPart("pFiles[]", file.getName(), photo);
        }

        productBody = productBuilder.build();
        Call<BaseResponse> call =
                NetController.getInstance().getProductService().registerProductBody(productBody);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == HTTP_OK) {
                    _view.onSuccessfullyAddedProduct();
                } else {
                    _view.onErrorUploadProduct(NetController.getError(response.errorBody()).msg);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (t instanceof UnknownHostException)
                    _view.onErrorUploadProduct("Internet not available");
                else
                    _view.onErrorUploadProduct("Unknown error occurred");
            }
        });
    }

    @Override
    public void addAuctionProduct(String accessToken, String productTitle, String productDesc,
                                  String categoryID, String subCategoryID, String amount, String quantiy,
                                  List<Image> imageList, String startingBid, String expiryDate, String guestList,
                                  String color, String size) {
        if (productTitle.isEmpty()) {
            _view.showProductTitleError();
            return;
        } else if (productDesc.isEmpty()) {
            _view.showProductDescError();
            return;
        } /*else if (amount.isEmpty()) {
            _view.showAmountError();
            return;
        } */else if (quantiy.isEmpty()) {
            _view.showQuantityError();
            return;
        } else if (imageList.size() == 0) {
            _view.showImageError();
            return;
        } else if (startingBid.isEmpty()) {
            _view.showStartingBidError();
            return;
        } else if (expiryDate.isEmpty()) {
            _view.showExpiryDateError();
            return;
        }

        _view.onLoading();
        RequestBody productBody;
        MultipartBody.Builder productBuilder;
        productBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("product_title", productTitle)
                .addFormDataPart("product_desc", productDesc)
                .addFormDataPart("product_price", amount)
                .addFormDataPart("product_qty", quantiy)
                .addFormDataPart("category_id", categoryID)
                .addFormDataPart("subcategory_id", subCategoryID)
                .addFormDataPart("access_token", accessToken)
                .addFormDataPart("auctionable", "1")
                .addFormDataPart("starting_bid", startingBid)
                .addFormDataPart("exp_date", expiryDate)
                .addFormDataPart("guest_list", guestList)
                .addFormDataPart("color", color)
                .addFormDataPart("size", size);

        for (Image image : imageList) {
            File file = new File(image.getPath());
            RequestBody photo = RequestBody.create(MediaType.parse("application/image"), file);
            productBuilder.addFormDataPart("pFiles[]", file.getName(), photo);
        }

        productBody = productBuilder.build();
        Call<BaseResponse> call =
                NetController.getInstance().getProductService().registerProductBody(productBody);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == HTTP_OK) {
                    _view.onSuccessfullyAddedProduct();
                } else {
                    _view.onErrorUploadProduct(NetController.getError(response.errorBody()).msg);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (t instanceof UnknownHostException)
                    _view.onErrorUploadProduct("Internet not available");
                else
                    _view.onErrorUploadProduct("Unknown error occurred");
            }
        });
    }

    @Override
    public void updateProduct(String isAuction, String accessToken, String productTitle, String productDesc,
                              String categoryID, String subCategoryID, String amount, String quantiy,
                              List<Image> imageList, String startingBid, String expiryDate, String guestList,
                              String imagesToDelete, String productID, String color, String size) {
        if (productTitle.isEmpty()) {
            _view.showProductTitleError();
            return;
        } else if (productDesc.isEmpty()) {
            _view.showProductDescError();
            return;
        } else if (amount.isEmpty()) {
            if (isAuction.equals("0")){
                _view.showAmountError();
                return;
            }
        } else if (quantiy.isEmpty()) {
            _view.showQuantityError();
            return;
        } else if (startingBid.isEmpty()) {
            if (isAuction.equals("1")){
                _view.showStartingBidError();
                return;
            }
        } else if (expiryDate.isEmpty()) {
            if (isAuction.equals("1")){
                _view.showExpiryDateError();
                return;
            }
        }

        _view.onLoading();
        RequestBody productBody;
        MultipartBody.Builder productBuilder;
        productBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("product_id", productID)
                .addFormDataPart("product_title", productTitle)
                .addFormDataPart("product_desc", productDesc)
                .addFormDataPart("product_price", amount)
                .addFormDataPart("product_qty", quantiy)
                .addFormDataPart("category_id", categoryID)
                .addFormDataPart("subcategory_id", subCategoryID)
                .addFormDataPart("access_token", accessToken)
                .addFormDataPart("color", color)
                .addFormDataPart("size", size);

        if (isAuction.equals("1")) {
            productBuilder.addFormDataPart("auctionable", "1")
                    .addFormDataPart("starting_bid", startingBid)
                    .addFormDataPart("exp_date", expiryDate)
                    .addFormDataPart("invite_link", "http://pocket-shops.com")
                    .addFormDataPart("guest_list", guestList);
        }
        else {
            productBuilder.addFormDataPart("auctionable", "0")
                    .addFormDataPart("starting_bid", "0")
                    .addFormDataPart("exp_date", "1996/01/23")
                    .addFormDataPart("invite_link", "http://pocket-shops.com")
                    .addFormDataPart("guest_list", "0");
          //  productBuilder.addFormDataPart("invite_link", "");
        }

        if (!imagesToDelete.isEmpty()){
            productBuilder.addFormDataPart("imagesToDelete", imagesToDelete);
        }

        for (Image image : imageList) {
            File file = new File(image.getPath());
            RequestBody photo = RequestBody.create(MediaType.parse("application/image"), file);
            productBuilder.addFormDataPart("pFiles[]", file.getName(), photo);
        }

        productBody = productBuilder.build();
        Call<ProductResponse> call =
                NetController.getInstance().getProductService().updateProductBody(productBody);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.code() == HTTP_OK) {
                    _view.onSuccessfullyUpdateProduct();
                } else {
                    _view.onErrorUploadProduct(NetController.getError(response.errorBody()).msg);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                if (t instanceof UnknownHostException)
                    _view.onErrorUploadProduct("Internet not available");
                else
                    _view.onErrorUploadProduct("Unknown error occurred");
            }
        });
    }
}