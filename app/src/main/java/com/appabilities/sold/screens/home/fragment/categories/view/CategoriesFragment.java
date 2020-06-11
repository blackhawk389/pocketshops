package com.appabilities.sold.screens.home.fragment.categories.view;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseFragment;
import com.appabilities.sold.databinding.CategoryItemLayoutBinding;
import com.appabilities.sold.databinding.FragmentCategoriesBinding;
import com.appabilities.sold.networking.response.CategoriesResponse;
import com.appabilities.sold.screens.home.fragment.categories.CategoriesContract;
import com.appabilities.sold.screens.home.fragment.categories.CategoriesPresenter;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends BaseFragment implements CategoriesContract.View<List<CategoriesResponse>>, Function2<CategoriesResponse, Integer, Unit> {


    FragmentCategoriesBinding bi;
    CategoriesPresenter presenter;
    RecyclerAdapterUtil<CategoriesResponse> mCategoriesAdapter;
    List<CategoriesResponse> listCategories;

    public CategoriesFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bi = DataBindingUtil.inflate(inflater, R.layout.fragment_categories, container, false);
        bi.setView(this);
        presenter = new CategoriesPresenter(this);
        presenter.initScreen();
        bi.setPresenter(presenter);
        return bi.getRoot();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    @Override
    public void setupViews() {
        listCategories = new ArrayList<>();
        mCategoriesAdapter = new RecyclerAdapterUtil<>(
                mActivity, listCategories, R.layout.category_item_layout);
        mCategoriesAdapter.addOnDataBindListener(new Function4<View, CategoriesResponse, Integer, Map<Integer, ? extends View>, Unit>() {
            @Override
            public Unit invoke(View view, CategoriesResponse categoriesResponse, Integer integer, Map<Integer, ? extends View> integerMap) {
                CategoryItemLayoutBinding binding = DataBindingUtil.bind(view);
                binding.setCategoryModel(categoriesResponse);
                binding.executePendingBindings();
                return null;
            }
        });
        mCategoriesAdapter.addOnClickListener(this);

        bi.categoriesRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        bi.categoriesRecycler.setLayoutManager(layoutManager);
        bi.categoriesRecycler.setAdapter(mCategoriesAdapter);

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
    public Unit invoke(CategoriesResponse categoriesResponse, Integer integer) {
        if (categoriesResponse.subcategories != null && categoriesResponse.subcategories.size() > 0){
            Intent intent = new Intent(mActivity, SubCategoryActivity.class);
            intent.putExtra(AppConstants.KEYS.CATEGORY_ITEM.name(), Parcels.wrap(categoriesResponse));
            startActivity(intent);
        }else {
            Intent intent = new Intent(mActivity, ShowProductsActivity.class);
            intent.putExtra(AppConstants.KEYS.IS_FROM_CATEGORY.name(), true);
            intent.putExtra(AppConstants.KEYS.CATEGORY_ID.name(), categoriesResponse.categoryID);
            intent.putExtra(AppConstants.KEYS.CATEGORY_NAME.name(), categoriesResponse.categoryName);
            startActivity(intent);
        }
        return null;
    }
}
