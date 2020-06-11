package com.appabilities.sold.screens.user_profile.fragment.my_product;

/**
 * Created by Hamza on 11/3/2017.
 */
public interface MyProductListContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View<T> {
        void setupViews();

        void onLoading();

        void onRequestSuccessful(T responseData);

        void onRequestFail(String errorMessage);

        void onRequestNotFound();
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void getProductList(String accessToken);
    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}