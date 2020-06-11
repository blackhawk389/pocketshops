package com.appabilities.sold.screens.seller_profile;
import androidx.viewpager.widget.ViewPager;

import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.networking.response.SellerProfileResponse;
import com.appabilities.sold.networking.response.VerifyUserResponse;
import com.appabilities.sold.utils.CustomViewPager;

import java.util.List;

/**
 * Created by Hamza on 10/27/2017.
 */
public interface SellerProfileContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View{
        void setupViews();
        void setUpViewpager(ViewPager viewPager);
        void setUpTabIcons();
        void updateProfile(SellerProfileResponse body);
        void errorInGettingProfile();
        void onLoading();
        void onClickFollow();
        void onClickChat();
        void updateFollowStatus(VerifyUserResponse body);
        void errorFollowStatus();

    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void getProfile(String accessToken, String sellerID);
        void updateFollowStatus(String accessToken, String followType, String followUserID, String followStatus);


    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}