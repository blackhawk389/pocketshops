package com.appabilities.sold.screens.home.fragment.sell.view;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.BR;
import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseFragment;
import com.appabilities.sold.databinding.FragmentSellBinding;
import com.appabilities.sold.databinding.ProductItemLayoutBinding;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.screens.home.fragment.sell.SellContract;
import com.appabilities.sold.screens.home.fragment.sell.SellPresenter;
import com.appabilities.sold.screens.home.view.HomeActivity;
import com.appabilities.sold.screens.my_product_detail.view.MyProductDetailActivity;
import com.appabilities.sold.screens.product_detail.view.ProductDetailActivity;
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
 */
public class SellFragment extends BaseFragment implements SellContract.View<List<ProductResponse>>, Function4<View, ProductResponse, Integer, Map<Integer, ? extends View>, Unit>, Function2<ProductResponse, Integer, Unit> {

    FragmentSellBinding bi;
    SellPresenter presenter;
    RecyclerAdapterUtil<ProductResponse> mSellAdapter;
    List<ProductResponse> myProductList;
    LinearLayoutManager layoutManager;

    public SellFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bi = DataBindingUtil.inflate(inflater, R.layout.fragment_sell, container, false);
        bi.setView(this);
        presenter = new SellPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
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
        myProductList = new ArrayList<>();
        mSellAdapter = new RecyclerAdapterUtil<>(mActivity, myProductList, R.layout.product_item_layout);
        mSellAdapter.addOnDataBindListener(this);
        mSellAdapter.addOnClickListener(this);
        bi.recyclerSell.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        bi.recyclerSell.setLayoutManager(layoutManager);
        bi.recyclerSell.setAdapter(mSellAdapter);
        presenter.getMyProductList(((HomeActivity) mActivity).getLoginResponse().access_token);
    }

    @Override
    public void onLoading() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void onRequestSuccessful(List<ProductResponse> responseData) {
        mSellAdapter.getItemList().removeAll(mSellAdapter.getItemList());
        mSellAdapter.getItemList().addAll(responseData);
        mSellAdapter.notifyDataSetChanged();
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.initScreen();
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
        binding.setVariable(BR.productModel, productResponse);
        binding.executePendingBindings();

        binding.btnFavouriteProductitem.setVisibility(View.GONE);

        binding.ratingProductProductitem.setRating(Float.valueOf(productResponse.ratings));

        if (productResponse.auctionable.equals("1")) {
            binding.slantedAuctionableProductitem.setVisibility(View.VISIBLE);
        }
        if (productResponse.sponsered.equals("1"))
            binding.txtStatusSponser.setVisibility(View.VISIBLE);
        return null;
    }

    @Override
    public Unit invoke(ProductResponse productResponse, Integer position) {
        View view = layoutManager.findViewByPosition(position);
        SimpleDraweeView imgProduct = (SimpleDraweeView) view.findViewById(R.id.imgProduct_productitem);
        Intent intent = new Intent(mActivity, MyProductDetailActivity.class);
        intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
        startActivity(intent);
       /* int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            intent.putExtra(AppConstants.KEYS.TRANSITION_VIEW.name(), ViewCompat.getTransitionName(imgProduct));
            intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    mActivity,
                    imgProduct,
                    ViewCompat.getTransitionName(imgProduct));

            startActivity(intent, options.toBundle());
        } else {
            intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
            startActivity(intent);
        }*/
        return null;
    }
}
