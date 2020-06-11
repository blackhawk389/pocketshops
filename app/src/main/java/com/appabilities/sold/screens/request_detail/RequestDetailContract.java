package com.appabilities.sold.screens.request_detail;

import com.appabilities.sold.networking.response.BaseResponse;

/**
 * Created by Hamza on 10/31/2017.
 */
public interface RequestDetailContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View {
        void setupViews();
        void updateSlider();
        void onClickMakeOffer();
        void onClickChat();
        void onClickEdit();
        void onClickViewOffer();
        void onClickDelete();
        void showProfileOnClick();

        void requestDeletedSuccessfully(BaseResponse body);

        void errorInDeletingRequest();
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void deleteRequest(String accessToken, String productID);

    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}