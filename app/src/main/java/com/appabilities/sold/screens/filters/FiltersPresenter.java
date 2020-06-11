package com.appabilities.sold.screens.filters;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.CategoriesResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Wajahat on 6/22/2017.
 */
public class FiltersPresenter extends BasePresenter<FiltersContract.View> implements FiltersContract.Actions {

    HashMap<String, List<String>> categoriesMap;

    public FiltersPresenter(FiltersContract.View view) {
        super(view);
        categoriesMap = new HashMap<>();
    }

    @Override
    public void initScreen() {
        _view.setupViews();
        _view.setSortFilters();
        getCategories();
    }

    @Override
    public void getCategories() {
        Call<List<CategoriesResponse>> call = NetController.getInstance().getCategoriesService().getCategoriesList();
        call.enqueue(new Callback<List<CategoriesResponse>>() {
            @Override
            public void onResponse(Call<List<CategoriesResponse>> call, Response<List<CategoriesResponse>> response) {
                if (response.code() == HTTP_OK) {
                    _view.onCategoryRequestSuccessful(response.body());
                } else if (response.code() == HTTP_NOT_FOUND) {
                    _view.onCategoryRequestNotFound();
                } else {
                    _view.onCategoryRequestFail("");
                }
            }

            @Override
            public void onFailure(Call<List<CategoriesResponse>> call, Throwable t) {
                _view.onCategoryRequestFail("");
            }
        });
    }

    /*@Override
    public void getPartCategories()
    {
        //TODO: Load categories from server here

        final List<String> list = new ArrayList<>();
        //list.addAll(Arrays.asList("Interior", "Exterior", "Tyres", "Lights", "Engine", "Windows", "Doors"));

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
        dbref.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot subcat : dataSnapshot.getChildren())
                {
                    categoriesMap.put(subcat.getKey(), (List<String>) subcat.getValue());
                    list.add(subcat.getKey());
                }
                _view.setPartCategoryFilters(list);
                loadSubCategoriesOf("Any");
                _view.enableSelectedFilters();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
    }

    @Override
    public void loadSubCategoriesOf(final String categoryID) {
        //_view.showSubCatProgress();
        _view.setPartSubCategoryFilters(categoriesMap.get(categoryID));
    }*/

}