package com.appabilities.sold.screens.my_account;

import com.appabilities.sold.model.RevenueModel;
import com.appabilities.sold.networking.response.BaseResponse;

/**
 * Created by Hamza on 1/11/2018.
 */
public interface MyAccountContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View {
        void setupViews();
        void onClickMonth();
        void onClickYear();
        void onSuccess(RevenueModel body);
        void onSuccessWithdrawl(BaseResponse body);
        void onFailure();
        void onFailureWithdrawl();
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void getRevenues(String date, String accessToken);
        void requestWithdrawl(String accessToken, float amount);

    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}