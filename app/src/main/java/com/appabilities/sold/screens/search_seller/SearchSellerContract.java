package com.appabilities.sold.screens.search_seller;

import com.appabilities.sold.networking.response.VerifyUserResponse;

/**
 * Created by Hamza on 11/6/2017.
 */
public interface SearchSellerContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View<T> {
        void setupViews();

        void onLoading();

        void onRequestSuccessful(T responseData);

        void onRequestFail(String errorMessage);

        void onRequestNotFound();

        void getSellers(String offset);

        void updateFollowStatus(VerifyUserResponse body);

        void errorFollowStatus();
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void getSellers(String accessToken, String offset, String keyword);
        void updateFollowStatus(String accessToken, String followType, String followUserID, String followStatus);

    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}