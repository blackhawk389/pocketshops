package com.appabilities.sold.screens.user_profile;

import com.appabilities.sold.model.GetFollowers;
import com.appabilities.sold.networking.response.LoginResponse;

import java.io.File;

/**
 * Created by Hamza on 11/3/2017.
 */
public interface UserProfileContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View {
        void setupViews();
        void setUpViewpager();
        void onClickEditCover();
        void successfullyUpdateCover(LoginResponse loginData);
        void errorUpdateCover(String msg);
        void onGetFollowers(GetFollowers obj);
        void oGetFollowersError();
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void getFollowers(String accessToken);
        void postCover(String accessToken, File imgFile);

    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}