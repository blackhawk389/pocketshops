package com.appabilities.sold.screens.add_auction.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivityAddAuctionBinding;
import com.appabilities.sold.databinding.ProductItemLayoutBinding;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.screens.add_auction.AddAuctionContract;
import com.appabilities.sold.screens.add_auction.AddAuctionPresenter;
import com.appabilities.sold.screens.add_product.view.AddProductActivity;
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

public class AddAuctionActivity extends BaseActivity implements AddAuctionContract.View<List<ProductResponse>>, Function4<View, ProductResponse, Integer, Map<Integer, ? extends View>, Unit>, Function2<ProductResponse, Integer, Unit> {

    ActivityAddAuctionBinding bi;
    AddAuctionPresenter presenter;
    RecyclerAdapterUtil<ProductResponse> mProductAdapter;
    List<ProductResponse> mProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_add_auction);
        presenter = new AddAuctionPresenter(this);
        bi.setView(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("MAKE AUCTION");

        mProductList = new ArrayList<>();
        bi.recyclerAuctionProducts.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        bi.recyclerAuctionProducts.setLayoutManager(layoutManager);
        mProductAdapter = new RecyclerAdapterUtil<>(this, mProductList, R.layout.product_item_layout);
        bi.recyclerAuctionProducts.setAdapter(mProductAdapter);
        mProductAdapter.addOnDataBindListener(this);
        mProductAdapter.addOnClickListener(this);
        presenter.getProducts(loginResponse.access_token);
    }

    @Override
    public void onLoading() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void onRequestSuccessful(List<ProductResponse> responseData) {
        List<ProductResponse> nonAuctionList = new ArrayList<>();
        for (ProductResponse item :
                responseData) {
            if (!item.auctionable.equals("1")){
                nonAuctionList.add(item);
            }
        }
        mProductAdapter.getItemList().addAll(nonAuctionList);
        mProductAdapter.notifyDataSetChanged();
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
    public Unit invoke(View view, ProductResponse productResponse, Integer integer, Map<Integer, ? extends View> integerMap) {
        ProductItemLayoutBinding binding = DataBindingUtil.bind(view);
        binding.setProductModel(productResponse);
        binding.executePendingBindings();

        binding.btnFavouriteProductitem.setVisibility(View.GONE);
        binding.ratingProductProductitem.setRating(Float.valueOf(productResponse.ratings));
        return null;
    }

    @Override
    public Unit invoke(ProductResponse productResponse, Integer integer) {
        Intent intent = new Intent(this, AddProductActivity.class);
        intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
        intent.putExtra(AppConstants.KEYS.IS_FROM_AUCTION.name(), true);
        startActivity(intent);
        return null;
    }
}
