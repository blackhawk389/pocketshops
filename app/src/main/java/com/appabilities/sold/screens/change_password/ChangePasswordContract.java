package com.appabilities.sold.screens.change_password;

/**
 * Created by Hamza on 10/19/2017.
 */
public interface ChangePasswordContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View {
        void setupViews();
        void onClickChangePassword();
        void errorOldPass(String s);
        void errorNewPass(String s);
        void showError(String msg);
        void onSuccessfulChangePassword(String msg);
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void changePassword(String accessToken, String oldPass, String newPass);
    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}