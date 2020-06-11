package com.appabilities.sold.screens.add_offer;

import com.nguyenhoanglam.imagepicker.model.Image;

import java.util.List;

/**
 * Created by Hamza on 10/31/2017.
 */
public interface AddOfferContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View {
        void setupViews();
        void onClickAddOffer();
        void onClickGalleryForImages();
        void successfullyAddedOffer();
        void errorInAddingOffer();
        void enterOfferTitle(String s);
        void enterOfferDetail(String s);
        void enterOfferQuantity(String s);
        void enterOfferPrice(String s);
        void selectImagesForOffer(String s);
        void onLoading();
        void loadContent();

    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void addOffer(String productID, String offerTitle, String offerDetail,
                      String offerQty, String offerPrice, String accessToken,List<Image> mSelectedImages);
    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}