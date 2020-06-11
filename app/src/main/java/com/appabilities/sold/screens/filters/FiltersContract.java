package com.appabilities.sold.screens.filters;

import com.appabilities.sold.networking.response.CategoriesResponse;

import java.util.List;

/**
 * Created by Wajahat on 6/22/2017.
 */
public interface FiltersContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    public interface View {
        void setupViews();
        void setSortFilters();
        void btnApplyFiltersClick();
        void onCategoryRequestSuccessful(List<CategoriesResponse> body);
        void onCategoryRequestNotFound();
        void onCategoryRequestFail(String s);
        void updateCategoryAdapter();
        void updateSubCategoryAdapter();

    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    public interface Actions {
        public void initScreen();
        void getCategories();
    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    public interface Repository {

    }

}
