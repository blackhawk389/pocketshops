package com.appabilities.sold.screens.home.fragment.favourites;

import com.appabilities.sold.networking.response.VerifyUserResponse;

/**
 * Created by Hamza on 9/15/2017.
 */
public interface FavouriteContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View<T> {
        void setupViews();

        void onLoading();

        void onRequestSuccessful(T responseData);

        void onRequestFail(String errorMessage);

        void onRequestNotFound();

        void updateProductLikeStatus(VerifyUserResponse body, int status);

        void errorUpdateLikeStatus();
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void getUserFavouriteProduct(String accessToken);
        void onUpdateProductLike(String accessToken, String productID, int status);

    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}