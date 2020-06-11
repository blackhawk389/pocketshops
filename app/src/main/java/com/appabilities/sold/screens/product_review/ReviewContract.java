package com.appabilities.sold.screens.product_review;

/**
 * Created by Hamza on 10/16/2017.
 */
public interface ReviewContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View{
        void setupViews();
        void onClickSubmitReview();
        void showSuccessfullReviewSubmit();
        void showErrorSubmitReview();
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void submitReview(String accessToken,
                          String productID,
                          String rating,
                          String orderDID,
                          String comment);
    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}