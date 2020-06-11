package com.appabilities.sold.screens.offer;

import com.appabilities.sold.networking.response.OfferResponse;

/**
 * Created by Hamza on 11/1/2017.
 */
public interface OfferContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View<T> {
        void setupViews();

        void onLoading();

        void onRequestSuccessful(T responseData);

        void onRequestFail(String errorMessage);

        void onRequestNotFound();

        void offerAcceptedSuccessfully(OfferResponse offerResponse);

        void errorInAcceptingOffer();
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void getOffers(String accessToken, String productID);
        void acceptOffer(String accessToken, OfferResponse offerResponse);
        //void acceptOffer(String accessToken, String productID, String offerID);

    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}