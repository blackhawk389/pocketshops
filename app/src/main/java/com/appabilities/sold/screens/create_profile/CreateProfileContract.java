package com.appabilities.sold.screens.create_profile;

import android.content.Context;

import com.appabilities.sold.networking.response.LoginResponse;

import java.io.File;

/**
 * Created by Hamza on 9/14/2017.
 */
public interface CreateProfileContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View<T> {
        void setupViews();
        void onLoading();
        void onRequestSuccessful(T responseData);
        void onRequestFail(String errorMessage);
        void onNextCreateProfileClick();
        void onSkipClick();
        void onProfileClick();
        void onDropDownClick();
        void setUserInfo();
        Context getContext();
        void onUpdateSuccessful(LoginResponse body);
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void registerUser(String username, String email,
                          String password, String displayName,
                          String address, String phoneNo,
                          String country, String region,
                          String description, File avatarFile);

        void updateUser(String accessToken, String displayName,
                          String address, String phoneNo,
                          String country, String region,
                          String description, File avatarFile);

    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}