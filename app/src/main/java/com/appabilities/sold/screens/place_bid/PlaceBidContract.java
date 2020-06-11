package com.appabilities.sold.screens.place_bid;

/**
 * Created by Hamza on 10/19/2017.
 */
public interface PlaceBidContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View {
        void setupViews();
        void onClickPlaceBid();
        void successfullyPlaceBid(String msg);
        void errorInPlaceBid(String error);
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void placeBid(String accessToken, String productID, String bidPrice);
    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}