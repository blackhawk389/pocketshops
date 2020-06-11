package com.appabilities.sold.screens.settings;

/**
 * Created by Hamza on 10/19/2017.
 */
public interface SettingsContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View {
        void setupViews();
        void onClickChangeCategories();
        void onClickChangePassword();
        void onClickRefundPolicy();
        void onClickTermsConditions();
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();

    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}