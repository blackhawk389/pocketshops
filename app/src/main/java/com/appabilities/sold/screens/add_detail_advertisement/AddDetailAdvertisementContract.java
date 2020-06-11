package com.appabilities.sold.screens.add_detail_advertisement;

import android.net.Uri;

import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.PaymentResponse;

import java.io.File;

/**
 * Created by Hamza on 10/26/2017.
 */
public interface AddDetailAdvertisementContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View {
        void setupViews();
        void onClickUploadImage();
        void getImageFromGallery();
        void onClickSubmitAdvertisement();
        String getRealPathFromURI(Uri uri);
        void imageNotSelected();
        void onSuccessfullSubmitAdvert(BaseResponse body);
        void errorInSubmitAdvert();
        void titleError(String s);
        void invalidURL(String s);
        void updateCheckoutID(PaymentResponse body);
        void errorInGettingCheckoutID();
        void onError(String error);
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void submitAdvertisement(String accessToken,
                                 String advertID,
                                 String advertNoDays,
                                 String advertRate,
                                 String productID,
                                 String total,
                                 File advertImg,
                                 String url,
                                 String title,
                                 String desc);
        void getCheckoutID(String totalAmount, String userID);
    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}