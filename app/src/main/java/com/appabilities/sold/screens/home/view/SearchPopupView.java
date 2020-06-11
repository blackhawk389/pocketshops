package com.appabilities.sold.screens.home.view;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.appabilities.sold.R;
import com.appabilities.sold.databinding.SearchDialogLayoutBinding;
import com.appabilities.sold.model.SearchModel;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.CategoriesResponse;
import com.appabilities.sold.screens.home.SearchPopupInterface;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.meetic.marypopup.MaryPopup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Hamza on 9/21/2017.
 */

public class SearchPopupView {
    public final String TAG = SearchPopupView.class.getSimpleName();
    MaryPopup maryPopup;
    Activity activity;
    int layoutResourceId;
    View originView;
    ViewDataBinding binding;
    SearchPopupInterface searchListener;
    SearchDialogLayoutBinding bi;
    List<String> listCategories;
    List<String> listSubCategories;
    List<CategoriesResponse> categories;
    int selectedPositionCategory = -1;
    int selectedPositionSubCategory = -1;
    String sortType = "rating";


    public SearchPopupView(Activity activity, int layoutResourceId, View originView) {
        this.activity = activity;
        this.layoutResourceId = layoutResourceId;
        this.originView = originView;
        initPopup();
        initViews();
        getCategories();
    }

    public void initPopup() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(activity), layoutResourceId, null, false);
        maryPopup = MaryPopup.with(activity)
                .cancellable(true)
                .blackOverlayColor(Color.parseColor("#DD444444"))
                .backgroundColor(Color.parseColor("#EFF4F5"))
                .content(binding.getRoot())
                .center(true)
                .from(originView);

    }

    private void initViews() {
        bi = (SearchDialogLayoutBinding) binding;
        bi.rangePriceSearchpopup.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                bi.txtPriceRangeSearchpopup.setText("SAR " + minValue + " - " + "SAR " + maxValue);
            }
        });

        bi.spinnerCategorySearchpopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPositionCategory = position;
                listSubCategories = new ArrayList<String>();
                if (categories.get(position).subcategories != null && categories.get(position).subcategories.size() > 0) {
                    for (int i = 0; i < categories.get(position).subcategories.size(); i++) {
                        listSubCategories.add(categories.get(position).subcategories.get(i).categoryName);
                    }
                } else {
                    listSubCategories.add("None");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
                        android.R.layout.simple_dropdown_item_1line, listSubCategories);
                bi.spinnerSubCategorySearchpopup.setAdapter(adapter);
                bi.spinnerSubCategorySearchpopup.setText("");
                bi.spinnerSubCategorySearchpopup.clearListSelection();
            }
        });

        bi.spinnerSubCategorySearchpopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPositionSubCategory = position;
            }
        });

        bi.imgDropDownCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bi.spinnerCategorySearchpopup.showDropDown();
            }
        });

        bi.imgDropDownSubCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bi.spinnerSubCategorySearchpopup.showDropDown();
            }
        });

        bi.spinnerCategorySearchpopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bi.spinnerCategorySearchpopup.showDropDown();
            }
        });

        bi.spinnerSubCategorySearchpopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bi.spinnerSubCategorySearchpopup.showDropDown();
            }
        });

        bi.btnSearchProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchModel item = new SearchModel();
                if (selectedPositionCategory != -1)
                    item.setCategoryID(Integer.parseInt(categories.get(selectedPositionCategory).categoryID));
                if (selectedPositionSubCategory != -1)
                    item.setSubCategoryID(Integer.parseInt(
                                    categories.get(selectedPositionCategory).
                                            subcategories.get(selectedPositionSubCategory).categoryID));
                item.setMaxRange(bi.rangePriceSearchpopup.getSelectedMaxValue());
                item.setMinRange(bi.rangePriceSearchpopup.getSelectedMinValue());
                item.setSearchSellerName(bi.txtSellerNameSearchpopup.getText().toString().trim());
                item.setSearchText(bi.txtKeywordsSearchpopup.getText().toString().trim());
                item.setSortType(sortType);
                if (searchListener != null)
                    searchListener.OnSearchClicked(item);
            }
        });


        bi.spinnerSortType.setAdapter(new ArrayAdapter<String>(activity,
                android.R.layout.simple_dropdown_item_1line, activity.getResources().getStringArray(R.array.sort_type)));
        bi.imgSortType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bi.spinnerSortType.showDropDown();
            }
        });

        bi.spinnerSortType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    sortType = "rating";
                else if (position == 1)
                    sortType = "date";
                else if (position == 2)
                    sortType = "name";
            }
        });


    }

    public void setSearchListener(SearchPopupInterface listener) {
        searchListener = listener;
    }

    public void show() {
        maryPopup.show();
    }

    public boolean isOpened() {
        return maryPopup.isOpened();
    }

    public void close(boolean withScaleDown) {
        maryPopup.close(withScaleDown);
    }

    public void dismiss() {
        close(true);
    }

    private void getCategories() {
        Call<List<CategoriesResponse>> callGetCategories =
                NetController.getInstance().getCategoriesService().getCategoriesList();
        callGetCategories.enqueue(new Callback<List<CategoriesResponse>>() {
            @Override
            public void onResponse(Call<List<CategoriesResponse>> call,
                                   Response<List<CategoriesResponse>> response) {
                bi.progress.setVisibility(View.GONE);
                if (response.code() == HTTP_OK) {
                    bi.layoutContent.setVisibility(View.VISIBLE);
                    categories = response.body();
                    listCategories = new ArrayList<String>();
                    for (int i = 0; i < response.body().size(); i++) {
                        listCategories.add(response.body().get(i).categoryName);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
                            android.R.layout.simple_dropdown_item_1line, listCategories);
                    bi.spinnerCategorySearchpopup.setAdapter(adapter);
                } else if (response.code() == HTTP_NOT_FOUND) {
                    bi.txtErrorLayout.setText("No Categories Found");
                } else {
                    bi.txtErrorLayout.setText("Whoops! Something went wrong");
                }
            }

            @Override
            public void onFailure(Call<List<CategoriesResponse>> call, Throwable t) {
                bi.progress.setVisibility(View.GONE);
                bi.txtErrorLayout.setText("Whoops! Something went wrong");
            }
        });
    }
}
