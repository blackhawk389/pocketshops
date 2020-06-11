package com.appabilities.sold.screens.product_detail;

import com.appabilities.sold.networking.response.VerifyUserResponse;

/**
 * Created by Hamza on 10/2/2017.
 */
public interface ProductDetailContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View {
        void setupViews();
        void onClickBid();
        void onClickBuy();
        void onClickChat();
        void updateSlider();
        void updateProductLikeStatus(VerifyUserResponse body, int status);
        void errorUpdateLikeStatus();
        void onClickSellerProfile();
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void onUpdateProductLike(String accessToken, String productID, int status);

    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}