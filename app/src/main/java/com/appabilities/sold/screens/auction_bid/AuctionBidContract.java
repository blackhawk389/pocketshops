package com.appabilities.sold.screens.auction_bid;

import com.appabilities.sold.utils.AppConstants;

/**
 * Created by Hamza on 10/23/2017.
 */
public interface AuctionBidContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View<T> {
        void setupViews();

        void onLoading();

        void onRequestSuccessful(T responseData);

        void onRequestFail(String errorMessage);

        void onRequestNotFound();

        void setupAuctionStatus(AppConstants.AUCTION_STATUS status);

    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void getBidList(String accessToken, String productID);

    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}