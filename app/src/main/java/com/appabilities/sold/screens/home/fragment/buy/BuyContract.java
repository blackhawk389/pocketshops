package com.appabilities.sold.screens.home.fragment.buy;

import com.appabilities.sold.networking.response.AdvertisementResponse;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.networking.response.VerifyUserResponse;

import java.util.List;

/**
 * Created by Hamza on 9/15/2017.
 */
public interface BuyContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View{
        void setupViews();
        void onMoreTopProductsClick();
        void onMoreRecentProductsClick();
        void updateAdvertisement(List<AdvertisementResponse> adData, boolean status);
        void advertisementError();
        void updateTopProducts(List<ProductResponse> listProducts);
        void showEmptyTopProducts();
        void showErrorTopProducts();
        void updateRecentProducts(List<ProductResponse> listProducts);
        void showEmptyRecentProducts();
        void showErrorRecentProducts();
        void showContent();
        void updateProductLikeStatus(VerifyUserResponse body, int status);
        void errorUpdateLikeStatus();
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void getAdvertisement();
        void getTopProducts(String accessToken);
        void getRecentProducts(String accessToken);
        void onUpdateProductLike(String accessToken, String productID, int status);
    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}