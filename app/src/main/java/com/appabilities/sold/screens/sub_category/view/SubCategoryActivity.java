package com.appabilities.sold.screens.sub_category.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivitySubCategoryBinding;
import com.appabilities.sold.databinding.SubcategoryItemLayoutBinding;
import com.appabilities.sold.networking.response.CategoriesResponse;
import com.appabilities.sold.networking.response.SubCategoryResponse;
import com.appabilities.sold.screens.show_products.view.ShowProductsActivity;
import com.appabilities.sold.screens.sub_category.SubCategoryContract;
import com.appabilities.sold.screens.sub_category.SubCategoryPresenter;
import com.appabilities.sold.utils.AppConstants;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;

import org.parceler.Parcels;

import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;

public class SubCategoryActivity extends BaseActivity implements
        SubCategoryContract.View, Function4<View,
        SubCategoryResponse, Integer, Map<Integer, ? extends View>,
        Unit>, Function2<SubCategoryResponse, Integer, Unit> {

    ActivitySubCategoryBinding bi;
    SubCategoryPresenter presenter;
    CategoriesResponse categoriesResponse;
    RecyclerAdapterUtil<SubCategoryResponse> mSubCategoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_sub_category);
        presenter = new SubCategoryPresenter(this);
        bi.setView(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        categoriesResponse = Parcels.unwrap(
                getIntent().getParcelableExtra(AppConstants.KEYS.CATEGORY_ITEM.name()));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(categoriesResponse.categoryName);

        mSubCategoryAdapter = new RecyclerAdapterUtil<>(this, categoriesResponse.subcategories, R.layout.subcategory_item_layout);
        bi.recyclerCategories.setLayoutManager(new LinearLayoutManager((SubCategoryActivity.this)));
        bi.recyclerCategories.setItemAnimator(new DefaultItemAnimator());
        bi.recyclerCategories.setAdapter(mSubCategoryAdapter);

        mSubCategoryAdapter.addOnDataBindListener(this);
        mSubCategoryAdapter.addOnClickListener(this);
    }

    @Override
    public Unit invoke(View view, SubCategoryResponse subCategoryResponse, Integer position, Map<Integer, ? extends View> integerMap) {
        SubcategoryItemLayoutBinding binding = DataBindingUtil.bind(view);
        binding.setSubCategoryItem(subCategoryResponse);
        binding.executePendingBindings();
        return null;
    }

    @Override
    public Unit invoke(SubCategoryResponse subCategoryResponse, Integer position) {
        Intent intent = new Intent(SubCategoryActivity.this, ShowProductsActivity.class);
        intent.putExtra(AppConstants.KEYS.IS_FROM_CATEGORY.name(), false);
        intent.putExtra(AppConstants.KEYS.CATEGORY_ID.name(), categoriesResponse.categoryID);
        intent.putExtra(AppConstants.KEYS.CATEGORY_NAME.name(), categoriesResponse.categoryName);
        intent.putExtra(AppConstants.KEYS.SUB_CATEGORY_ITEM.name(), Parcels.wrap(subCategoryResponse));
        startActivity(intent);
        return null;
    }
}
