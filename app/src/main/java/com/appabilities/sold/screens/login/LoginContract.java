package com.appabilities.sold.screens.login;

import android.content.Context;

/**
 * Created by Hamza on 9/13/2017.
 */
public interface LoginContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View<T> {
        void setupViews();
        void onLoading();
        void onRequestSuccessful(T responseData);
        void onRequestFail(String errorMessage);
        void onLoginClick();
        void onResetPasswordClick();
        void onRegisterHereClick();
        void showUsernameError();
        void showPasswordError();
        Context getContext();
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void onLoginButtonClick(String username, String password);

    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}