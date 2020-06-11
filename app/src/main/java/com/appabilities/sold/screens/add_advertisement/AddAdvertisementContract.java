package com.appabilities.sold.screens.add_advertisement;

import com.appabilities.sold.networking.response.AdCategoryResponse;
import com.appabilities.sold.networking.response.ProductResponse;

import java.util.List;

/**
 * Created by Hamza on 10/25/2017.
 */
public interface AddAdvertisementContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View {
        void setupViews();

        void onRequestSuccessfull(List<ProductResponse> body);

        void onRequestNotFound();

        void onRequestError();

        void onLoading();

        void onPublicAddClick();

        void updateAdvertisementCategory(List<AdCategoryResponse> body);
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void getProducts(String accessToken);
        void getAdvertisementDetails();

    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}