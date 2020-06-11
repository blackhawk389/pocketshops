package com.appabilities.sold.screens.add_request;

import com.appabilities.sold.networking.response.RequestResponse;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.util.List;

/**
 * Created by Hamza on 10/30/2017.
 */
public interface AddRequestContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View<T> {
        void setupViews();

        void onLoading();

        void onRequestSuccessful(T responseData);

        void onRequestFail(String errorMessage);

        void onRequestNotFound();

        void updateSubCategories();

        void onClickShowCategory();

        void onClickCategoryDropDown();

        void onClickShowSubCategory();

        void onClickSubCategoryDropDown();

        void onClickGalleryForImages();

        void onClickAddRequest();

        void showRequestTitleError();

        void showRequestDescError();

        void showQuantityError();

        void showImageError();


        void onSuccessfullyAddedRequest();

        void onErrorUploadRequest(String errorMsg);

        void updateRequest(RequestResponse requestResponse);

        void updatedRequestSuccessfully();

        void errorRequestUpdate(String errorMsg);
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        void initScreen();

        void getCategories();

        void addRequest(String accessToken, String productTitle, String productDesc, String categoryID,
                        String subCategoryID, String amount, String quantiy, List<Image> imageList, String color, String size);

        void editRequest(String accessToken, String productTitle, String productDesc, String categoryID,
                         String subCategoryID, String amount, String quantiy, List<Image> imageList,
                         String productID, String imagesToDeleteID, String color, String size);
    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}