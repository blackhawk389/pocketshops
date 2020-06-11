package com.appabilities.sold.screens.add_offer;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 10/31/2017.
 */
public class AddOfferPresenter extends BasePresenter<AddOfferContract.View> implements AddOfferContract.Actions {

    public AddOfferPresenter(AddOfferContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void addOffer(String productID, String offerTitle, String offerDetail, String offerQty, String offerPrice, String accessToken,List<Image> mSelectedImages) {

        _view.onLoading();
        if (offerTitle.isEmpty()){
            _view.enterOfferTitle("Input Offer Title");
            _view.loadContent();
            return;
        }else if (offerDetail.isEmpty()){
            _view.enterOfferDetail("Input Offer Detail");
            _view.loadContent();
            return;
        }else if (offerQty.isEmpty()){
            _view.enterOfferQuantity("Input Offer Quantity");
            _view.loadContent();
            return;
        }else if (offerPrice.isEmpty()){
            _view.enterOfferPrice("Input Offer Price");
            _view.loadContent();
            return;
        }else if (mSelectedImages.size() == 0){
            _view.selectImagesForOffer("Select images to add offer");
            _view.loadContent();
            return;
        }

        RequestBody offerBody;
        MultipartBody.Builder productBuilder;
        productBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("product_id", productID)
                .addFormDataPart("offer_title", offerTitle)
                .addFormDataPart("offer_detail", offerDetail)
                .addFormDataPart("offer_qty", offerQty)
                .addFormDataPart("offer_price", offerPrice)
                .addFormDataPart("access_token", accessToken);


        for (Image image : mSelectedImages) {
            File file = new File(image.getPath());
            RequestBody photo = RequestBody.create(MediaType.parse("application/image"), file);
            productBuilder.addFormDataPart("pFiles[]", file.getName(), photo);
        }

        offerBody = productBuilder.build();

        Call<BaseResponse> callAddOffer = NetController.getInstance()
                .getRequestService().addOfferBody(offerBody);

        callAddOffer.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == HTTP_OK){
                    _view.successfullyAddedOffer();
                }else {
                    _view.errorInAddingOffer();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                _view.errorInAddingOffer();
            }
        });

    }
}