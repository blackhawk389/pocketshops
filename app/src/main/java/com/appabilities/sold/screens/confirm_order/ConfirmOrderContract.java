package com.appabilities.sold.screens.confirm_order;

import com.appabilities.sold.model.OrderModel;
import com.appabilities.sold.networking.response.PaymentResponse;
import com.appabilities.sold.networking.response.VATResponse;

/**
 * Created by Hamza on 10/13/2017.
 */
public interface ConfirmOrderContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View {
        void setupViews();
        void updateUI();
        void onClickPlaceOrder();
        void successfullySubmitOrder();
        void errorSubmitOrder();
        void showNameError(String s);
        void showEmailError(String s);
        void showPhoneError(String s);
        void showAddressError(String s);
        void deleteShoppingCartItem(String productID);
        void updateCheckoutID(PaymentResponse body);
        void errorInGettingCheckoutID();

        void onGetSettingResponse(VATResponse vat);
        void onFailed(String error);
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void placeOrder(OrderModel orderModel);
        void getCheckoutID(String totalAmount, String userID);

        void callGetSetting();

    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}