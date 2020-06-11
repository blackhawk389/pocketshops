package com.appabilities.sold.screens.request.view.fragment.user_request;

/**
 * Created by Hamza on 10/30/2017.
 */
public interface UserRequestContract {

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
        void getRequest(String accessToken);

    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}