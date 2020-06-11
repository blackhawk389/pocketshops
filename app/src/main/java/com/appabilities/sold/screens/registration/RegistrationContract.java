package com.appabilities.sold.screens.registration;

/**
 * Created by Hamza on 9/14/2017.
 */
public interface RegistrationContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View<T> {
        void setupViews();
        void onLoading();
        void onRequestSuccessful(T responseData);
        void onRequestFail(String errorMessage);
        void onClickLoginHere();
        void onNextClick();
        void showUsernameError(String s);
        void showEmailError(String s);
        void showPasswordError(String s);
        void showConfirmPassError(String s);
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void verifyUser(String username, String email, String password, String confirmPassword);

    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}