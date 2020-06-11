package com.appabilities.sold.screens.add_product;

import com.nguyenhoanglam.imagepicker.model.Image;

import java.util.List;

/**
 * Created by Hamza on 10/10/2017.
 */
public interface AddProductContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View<T> {
        void setupViews();
        void onLoading();
        void onRequestSuccessful(T responseData);
        void onRequestFail(String errorMessage);
        void onRequestNotFound();
        void onClickAddProduct();
        void onClickCategoryDropDown();
        void onClickSubCategoryDropDown();
        void updateSubCategories();
        void onClickGalleryForImages();
        void onSuccessfullyAddedProduct();
        void onErrorUploadProduct(String errorMsg);
        void showProductTitleError();
        void showProductDescError();
        void showAmountError();
        void showQuantityError();
        void showImageError();
        void showStartingBidError();
        void showExpiryDateError();
        void onClickExpiryDate();
        void updateProductDetails();
        void onClickShowCategory();
        void onClickShowSubCategory();
        void onSuccessfullyUpdateProduct();
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();
        void getCategories();
        void addProduct(String accessToken, String productTitle, String productDesc, String categoryID,
                              String subCategoryID, String amount, String quantiy, List<Image> imageList, String color, String size);
        void addAuctionProduct(String accessToken, String productTitle, String productDesc, String categoryID,
                        String subCategoryID, String amount, String quantiy, List<Image> imageList, String startingBid,
                               String expiryDate, String guestList, String color, String size);

        void updateProduct(String isAuction, String accessToken, String productTitle, String productDesc,
                           String categoryID, String subCategoryID, String amount, String quantity,
                           List<Image> imageList, String startingBid, String expiryDate, String guestList,
                           String imagesToDelete, String productID, String color, String size);

    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}