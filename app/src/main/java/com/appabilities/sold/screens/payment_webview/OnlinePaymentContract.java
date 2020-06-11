package com.appabilities.sold.screens.payment_webview;

import com.appabilities.sold.model.OrderModel;

import java.io.File;

/**
 * Created by Admin on 1/23/2019.
 */

public interface OnlinePaymentContract {

    public interface View {
        void setupViews();
        void successfullySubmitOrder();
        void successfullySubmitAdvertise();
        void errorSubmitOrder();
        void deleteShoppingCartItem(String productID);
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void placeOrder(OrderModel orderModel);

        void submitAdvertisement(String accessToken,
                                 String advertID,
                                 String advertNoDays,
                                 String advertRate,
                                 String productID,
                                 String total,
                                 File advertImg,
                                 String url,
                                 String title,
                                 String desc);
    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }
}
