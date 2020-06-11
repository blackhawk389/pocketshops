package com.appabilities.sold.screens.categories.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.sidemenu.SideMenuBaseActivity;
import com.appabilities.sold.databinding.ActivityCategoriesBinding;
import com.appabilities.sold.databinding.CategoryItemLayoutBinding;
import com.appabilities.sold.networking.response.CategoriesResponse;
import com.appabilities.sold.screens.categories.CategoriesContract;
import com.appabilities.sold.screens.categories.CategoriesPresenter;
import com.appabilities.sold.screens.show_products.view.ShowProductsActivity;
import com.appabilities.sold.screens.sub_category.view.SubCategoryActivity;
import com.appabilities.sold.utils.AppConstants;
import com.kennyc.view.MultiStateView;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;
import org.parceler.Parcels;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;

public class CategoriesActivity extends SideMenuBaseActivity implements CategoriesContract.View<List<CategoriesResponse>>, Function2<CategoriesResponse, Integer, Unit> {

    ActivityCategoriesBinding bi;
    CategoriesPresenter presenter;
    RecyclerAdapterUtil<CategoriesResponse> mCategoriesAdapter;
    List<CategoriesResponse> listCategories;

    private static final String TAG = CategoriesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = (ActivityCategoriesBinding) setContentLayout(R.layout.activity_categories);
        initDrawer(bi.toolbar, "CATEGORIES");
        bi.setView(this);
        presenter = new CategoriesPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        listCategories = new ArrayList<>();
        mCategoriesAdapter = new RecyclerAdapterUtil<>(
                this, listCategories, R.layout.category_item_layout);
        mCategoriesAdapter.addOnDataBindListener(new Function4<View, CategoriesResponse, Integer, Map<Integer, ? extends View>, Unit>() {
            @Override
            public Unit invoke(View view, final CategoriesResponse categoriesResponse, Integer integer, Map<Integer, ? extends View> integerMap) {
                CategoryItemLayoutBinding binding = DataBindingUtil.bind(view);
                binding.setCategoryModel(categoriesResponse);
                binding.executePendingBindings();

                return null;
            }
        });
        mCategoriesAdapter.addOnClickListener(this);
        bi.recyclerCategories.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        bi.recyclerCategories.setLayoutManager(layoutManager);
        bi.recyclerCategories.setAdapter(mCategoriesAdapter);

        presenter.getCategories();
    }

    @Override
    public void onLoading() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void onRequestSuccessful(List<CategoriesResponse> responseData) {
        mCategoriesAdapter.getItemList().addAll(responseData);
        mCategoriesAdapter.notifyDataSetChanged();
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
    }

    @Override
    public void onRequestFail(String errorMessage) {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void onRequestNotFound() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
    }

    @Override
    public void onResume() {
        super.onResume();
        selectMenuOption(R.id.nav_categories);
    }

    @Override
    public Unit invoke(CategoriesResponse categoriesResponse, Integer integer) {
        if (categoriesResponse.subcategories != null && categoriesResponse.subcategories.size() > 0){
            Intent intent = new Intent(this, SubCategoryActivity.class);
            intent.putExtra(AppConstants.KEYS.CATEGORY_ITEM.name(), Parcels.wrap(categoriesResponse));
            startActivity(intent);
        }else {
            Intent intent = new Intent(this, ShowProductsActivity.class);
            intent.putExtra(AppConstants.KEYS.IS_FROM_CATEGORY.name(), true);
            intent.putExtra(AppConstants.KEYS.CATEGORY_ID.name(), categoriesResponse.categoryID);
            intent.putExtra(AppConstants.KEYS.CATEGORY_NAME.name(), categoriesResponse.categoryName);
            startActivity(intent);
        }
        return null;
    }
}
