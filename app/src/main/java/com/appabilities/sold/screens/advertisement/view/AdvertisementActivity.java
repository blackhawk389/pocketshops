package com.appabilities.sold.screens.advertisement.view;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.sidemenu.SideMenuBaseActivity;
import com.appabilities.sold.databinding.ActivityAdvertisementBinding;
import com.appabilities.sold.databinding.AdvertismentItemLayoutBinding;
import com.appabilities.sold.networking.response.AdvertisementResponse;
import com.appabilities.sold.screens.add_advertisement.view.AddAdvertisementActivity;
import com.appabilities.sold.screens.advertisement.AdvertisementContract;
import com.appabilities.sold.screens.advertisement.AdvertisementPresenter;
import com.kennyc.view.MultiStateView;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function4;

public class AdvertisementActivity extends SideMenuBaseActivity implements AdvertisementContract.View<List<AdvertisementResponse>>, Function4<View, AdvertisementResponse, Integer, Map<Integer, ? extends View>, Unit> {

    AdvertisementPresenter presenter;
    ActivityAdvertisementBinding bi;
    RecyclerAdapterUtil<AdvertisementResponse> mAdvertisementAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = (ActivityAdvertisementBinding) setContentLayout(R.layout.activity_advertisement);
        bi.setView(this);
        presenter = new AdvertisementPresenter(this);
        presenter.initScreen();
        bi.setPresenter(presenter);
    }

    @Override
    public void setupViews() {
        initDrawer(bi.toolbar, "ADVERTISEMENT");

        mAdvertisementAdapter = new RecyclerAdapterUtil<>(this, new ArrayList<AdvertisementResponse>(), R.layout.advertisment_item_layout);
        mAdvertisementAdapter.addOnDataBindListener(this);
        bi.recyclerMyAdvertisement.setHasFixedSize(true);
        bi.recyclerMyAdvertisement.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        bi.recyclerMyAdvertisement.setAdapter(mAdvertisementAdapter);
        presenter.getAdvertisement(loginResponse.access_token);
    }

    @Override
    public void onLoading() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void onRequestSuccessful(List<AdvertisementResponse> responseData) {
        mAdvertisementAdapter.getItemList().addAll(responseData);
        mAdvertisementAdapter.notifyDataSetChanged();
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
    public void onClickAddAdvert() {
        startActivity(new Intent(AdvertisementActivity.this, AddAdvertisementActivity.class));
    }

    @Override
    public Unit invoke(View view, AdvertisementResponse advertisementResponse, Integer integer, Map<Integer, ? extends View> integerMap) {
        AdvertismentItemLayoutBinding binding = DataBindingUtil.bind(view);
        binding.setAdItem(advertisementResponse);
        binding.executePendingBindings();

        if (advertisementResponse.advertType.equals("Category")){
            binding.imgAd.setImageURI(advertisementResponse.productImg);
            binding.txtProductTitle.setText(advertisementResponse.productTitle);
        }else if (advertisementResponse.advertType.equals("Public")){
            binding.imgAd.setImageURI(advertisementResponse.advertImg);
            binding.txtProductTitle.setText(advertisementResponse.title);
        }
        binding.adType.setText(advertisementResponse.advertType);
        if (advertisementResponse.active.equals("false")) {
            ((GradientDrawable)binding.txtStatus.getBackground()).setColor(
                    ca.getResourceColor(R.color.colorAuctionOpen));
        } else {
            if (advertisementResponse.state.equals("expired")) {
                ((GradientDrawable)binding.txtStatus.getBackground()).setColor(
                        ca.getResourceColor(R.color.colorAuctionExpired));
            } else {
                ((GradientDrawable)binding.txtStatus.getBackground()).setColor(
                        ca.getResourceColor(R.color.colorAuctionApproved));
            }
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        selectMenuOption(R.id.nav_ad_history);
    }
}
