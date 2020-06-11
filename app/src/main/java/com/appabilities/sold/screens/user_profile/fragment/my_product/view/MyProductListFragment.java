package com.appabilities.sold.screens.user_profile.fragment.my_product.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseFragment;
import com.appabilities.sold.databinding.FragmentMyProductsBinding;
import com.appabilities.sold.databinding.ProductItemLayoutBinding;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.screens.my_product_detail.view.MyProductDetailActivity;
import com.appabilities.sold.screens.my_products.MyProductsPresenter;
import com.appabilities.sold.screens.user_profile.fragment.my_product.MyProductListContract;
import com.appabilities.sold.screens.user_profile.fragment.my_product.MyProductListPresenter;
import com.appabilities.sold.screens.user_profile.view.UserProfileActivity;
import com.appabilities.sold.utils.AppConstants;
import com.facebook.drawee.view.SimpleDraweeView;
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
 * Created by Hamza on 6/30/2017.
 */

public class MyProductListFragment extends BaseFragment implements
        MyProductListContract.View<List<ProductResponse>>,
        Function4<View, ProductResponse, Integer, Map<Integer, ? extends View>, Unit>, Function2<ProductResponse, Integer, Unit> {

    FragmentMyProductsBinding bi;
    MyProductListPresenter presenter;
    RecyclerAdapterUtil<ProductResponse> mAdapter;
    LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bi = DataBindingUtil.inflate(inflater,R.layout.fragment_my_products, container, false);
        bi.setView(this);
        presenter = new MyProductListPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
        return bi.getRoot();
    }


    @Override
    public void setupViews() {
        mAdapter = new RecyclerAdapterUtil<>(mActivity, new ArrayList<ProductResponse>(),R.layout.product_item_layout);
        mAdapter.addOnDataBindListener(this);
        mAdapter.addOnClickListener(this);
        bi.recyclerMyProducts.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        bi.recyclerMyProducts.setLayoutManager(layoutManager);
        bi.recyclerMyProducts.setAdapter(mAdapter);
        presenter.getProductList(((UserProfileActivity)mActivity).loginResponse.access_token);
    }

    @Override
    public void onLoading() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void onRequestSuccessful(List<ProductResponse> responseData) {
        mAdapter.getItemList().clear();
        mAdapter.getItemList().addAll(responseData);
        mAdapter.notifyDataSetChanged();
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

        binding.ratingProductProductitem.setRating(Float.valueOf(productResponse.ratings));
        binding.btnFavouriteProductitem.setVisibility(View.GONE);
        if (productResponse.auctionable.equals("1")){
            binding.slantedAuctionableProductitem.setVisibility(View.VISIBLE);
        }

        return null;
    }

    @Override
    public Unit invoke(ProductResponse productResponse, Integer integer) {
        View view = layoutManager.findViewByPosition(integer);
        SimpleDraweeView imgProduct = (SimpleDraweeView) view.findViewById(R.id.imgProduct_productitem);
        Intent intent = new Intent(mActivity, MyProductDetailActivity.class);
        intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
        startActivity(intent);
        /*int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            intent.putExtra(AppConstants.KEYS.TRANSITION_VIEW.name(), ViewCompat.getTransitionName(imgProduct));
            intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    mActivity,
                    imgProduct,
                    ViewCompat.getTransitionName(imgProduct));

            startActivity(intent, options.toBundle());
        }else {
            intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
            startActivity(intent);
        }*/

        return null;
    }
}
