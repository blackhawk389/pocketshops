package com.appabilities.sold.screens.forget_password;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Hamza on 7/19/2017.
 */
public interface ForgetPasswordContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View {
        public void setupViews();
        public void onResetButtonClick();
        void showProgress();
        void showSuccess(String msg);
        void showFailure();
        void showFailure(String message);
        void showEmailError(String s);
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        public void initScreen();
        public void resetPassword(String email);
    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}