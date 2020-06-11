package com.appabilities.sold.screens.add_advertisement.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivityAddAdvertisementBinding;
import com.appabilities.sold.databinding.ProductItemLayoutBinding;
import com.appabilities.sold.networking.response.AdCategoryResponse;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.screens.add_advertisement.AddAdvertisementContract;
import com.appabilities.sold.screens.add_advertisement.AddAdvertisementPresenter;
import com.appabilities.sold.screens.add_detail_advertisement.view.AddDetailAdvertActivity;
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

public class AddAdvertisementActivity extends BaseActivity implements
        AddAdvertisementContract.View, Function4<View, ProductResponse, Integer, Map<Integer, ? extends View>, Unit>, Function2<ProductResponse, Integer, Unit> {

    ActivityAddAdvertisementBinding bi;
    AddAdvertisementPresenter presenter;
    RecyclerAdapterUtil<ProductResponse> mProductAdapter;
    private String TAG = AddAdvertisementActivity.class.getSimpleName();
    List<String> adName;
    List<AdCategoryResponse> adCategoryItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_add_advertisement);
        bi.setView(this);
        presenter = new AddAdvertisementPresenter(this);
        presenter.initScreen();
        bi.setPresenter(presenter);
    }

    @Override
    public void setupViews() {

        presenter.getAdvertisementDetails();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ADD ADVERTISEMENT");

        mProductAdapter = new RecyclerAdapterUtil<>(
                this, new ArrayList<ProductResponse>(), R.layout.product_item_layout);
        mProductAdapter.addOnDataBindListener(this);
        mProductAdapter.addOnClickListener(this);
        bi.recylerProductsChooseproduct.setHasFixedSize(true);
        bi.recylerProductsChooseproduct.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        bi.recylerProductsChooseproduct.setAdapter(mProductAdapter);


        presenter.getProducts(loginResponse.access_token);
    }

    @Override
    public void onRequestSuccessfull(List<ProductResponse> body) {
        mProductAdapter.getItemList().addAll(body);
        mProductAdapter.notifyDataSetChanged();
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
    }

    @Override
    public void onRequestNotFound() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
    }

    @Override
    public void onRequestError() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void onLoading() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void onPublicAddClick() {
        Intent intent = new Intent(AddAdvertisementActivity.this, AddDetailAdvertActivity.class);
        intent.putExtra(AppConstants.KEYS.ADVERT_ITEM.name(),
                Parcels.wrap(adCategoryItem.get(0)));
        startActivity(intent);
    }

    @Override
    public void updateAdvertisementCategory(List<AdCategoryResponse> body) {
        adCategoryItem = body;
    }

    @Override
    public Unit invoke(View view, ProductResponse productResponse,
                       Integer integer, Map<Integer, ? extends View> integerMap) {
        ProductItemLayoutBinding binding = DataBindingUtil.bind(view);
        binding.setProductModel(productResponse);
        binding.executePendingBindings();
        binding.ratingProductProductitem.setRating(Float.valueOf(productResponse.ratings));
        binding.btnFavouriteProductitem.setVisibility(View.GONE);
        return null;
    }

    @Override
    public Unit invoke(ProductResponse productResponse, Integer integer) {
        Intent intent = new Intent(AddAdvertisementActivity.this, AddDetailAdvertActivity.class);
        intent.putExtra(AppConstants.KEYS.ADVERT_ITEM.name(),
                Parcels.wrap(adCategoryItem.get(1)));
        intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
        startActivity(intent);
        return null;
    }
}
