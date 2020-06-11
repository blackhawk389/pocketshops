package com.appabilities.sold.screens.auction.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.base.sidemenu.SideMenuBaseActivity;
import com.appabilities.sold.databinding.ActivityMyAuctionBinding;
import com.appabilities.sold.databinding.BidItemLayoutBinding;
import com.appabilities.sold.networking.response.UserBidResponse;
import com.appabilities.sold.screens.auction.MyAuctionContract;
import com.appabilities.sold.screens.auction.MyAuctionPresenter;
import com.appabilities.sold.screens.product_bids.view.ProductBidsActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.kennyc.view.MultiStateView;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;

public class MyAuctionActivity extends SideMenuBaseActivity implements MyAuctionContract.View<List<UserBidResponse>>, Function4<View, UserBidResponse, Integer, Map<Integer, ? extends View>, Unit> {

    ActivityMyAuctionBinding bi;
    MyAuctionPresenter presenter;
    RecyclerAdapterUtil<UserBidResponse> mBidAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = (ActivityMyAuctionBinding) setContentLayout(R.layout.activity_my_auction);
        bi.setView(this);
        presenter = new MyAuctionPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        initDrawer(bi.toolbar, "AUCTIONS");
        mBidAdapter = new RecyclerAdapterUtil<>(this, new ArrayList<UserBidResponse>(), R.layout.bid_item_layout);
        mBidAdapter.addOnDataBindListener(this);
        bi.recyclerMyBids.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        bi.recyclerMyBids.setLayoutManager(layoutManager);
        bi.recyclerMyBids.setAdapter(mBidAdapter);
        presenter.getBidList(loginResponse.access_token);
    }

    @Override
    public void onLoading() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void onRequestSuccessful(List<UserBidResponse> responseData) {
        mBidAdapter.getItemList().addAll(responseData);
        mBidAdapter.notifyDataSetChanged();
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
    public Unit invoke(View view, final UserBidResponse userBidResponse,
                       Integer integer, Map<Integer, ? extends View> integerMap) {
        BidItemLayoutBinding binding = DataBindingUtil.bind(view);
        binding.setBidItem(userBidResponse);
        binding.executePendingBindings();
        if (userBidResponse.subCategoryName == null
                || userBidResponse.subCategoryName.equals("null"))
            binding.txtCategory.setText(userBidResponse.categoryName);
        else
            binding.txtCategory.setText(userBidResponse.categoryName+"->"+userBidResponse.subCategoryName);

        binding.txtViewBids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAuctionActivity.this, ProductBidsActivity.class);
                intent.putExtra(AppConstants.KEYS.BIDS_ITEM.name(), Parcels.wrap(userBidResponse));
                startActivity(intent);
            }
        });

        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        selectMenuOption(R.id.nav_auctions);
    }
}
