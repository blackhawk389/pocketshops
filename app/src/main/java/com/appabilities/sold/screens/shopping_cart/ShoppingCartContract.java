package com.appabilities.sold.screens.shopping_cart;

/**
 * Created by Hamza on 10/12/2017.
 */
public interface ShoppingCartContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View<T> {
        void setupViews();
        void onLoading();
        void onRequestSuccessful(T responseData);
        void onRequestFail(String errorMessage);
        void onRequestNotFound();
        void refreshShoppingCart();
        void refreshShoppingCart(boolean state);
        void onClickCheckOut();
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void getCartItems();
    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}