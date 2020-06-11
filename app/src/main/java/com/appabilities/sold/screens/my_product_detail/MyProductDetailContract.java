package com.appabilities.sold.screens.my_product_detail;

import com.appabilities.sold.networking.response.BaseResponse;

/**
 * Created by Hamza on 10/3/2017.
 */
public interface MyProductDetailContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View {
        void setupViews();
        void updateSlider();
        void onClickShare();
        void onClickEdit();
        void onClickBids();
        void onClickDelete();
        void errorInDeletingProduct();
        void productDeletedSuccessfully(BaseResponse body);
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void deleteProduct(String accessToken, String productID);

    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}