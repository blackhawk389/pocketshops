package com.appabilities.sold.screens.my_sale.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.base.sidemenu.SideMenuBaseActivity;
import com.appabilities.sold.databinding.ActivityMySaleBinding;
import com.appabilities.sold.databinding.MySaleItemBinding;
import com.appabilities.sold.networking.response.SaleResponse;
import com.appabilities.sold.screens.home.view.HomeActivity;
import com.appabilities.sold.screens.my_sale.MySaleContract;
import com.appabilities.sold.screens.my_sale.MySalePresenter;
import com.appabilities.sold.screens.my_sale_detail.view.SaleDetailActivity;
import com.appabilities.sold.utils.AppConstants;
import com.kennyc.view.MultiStateView;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;

public class MySaleActivity extends SideMenuBaseActivity implements MySaleContract.View<List<SaleResponse>>, Function4<View, SaleResponse, Integer, Map<Integer, ? extends View>, Unit>, Function2<SaleResponse, Integer, Unit> {

    ActivityMySaleBinding bi;
    MySalePresenter presenter;
    RecyclerAdapterUtil<SaleResponse> mSaleAdapter;
    List<SaleResponse> saleList;
    boolean from_notification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = (ActivityMySaleBinding) setContentLayout(R.layout.activity_my_sale);
        bi.setView(this);
        presenter = new MySalePresenter(this);
        presenter.initScreen();

        if(getIntent().getExtras() != null){
            from_notification = getIntent().getExtras().getBoolean("from_notification");
        }

        bi.setPresenter(presenter);
    }

    @Override
    public void setupViews() {
        initDrawer(bi.toolbar, "SALES");
        saleList = new ArrayList<>();
        mSaleAdapter = new RecyclerAdapterUtil<>(this, saleList, R.layout.my_sale_item);
        mSaleAdapter.addOnDataBindListener(this);
        mSaleAdapter.addOnClickListener(this);

        bi.mySaleRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        bi.mySaleRecycler.setLayoutManager(layoutManager);
        bi.mySaleRecycler.setAdapter(mSaleAdapter);

        presenter.getSales(loginResponse.access_token);
        bi.refreshMysales.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getSales(loginResponse.access_token);
            }
        });
    }

    @Override
    public void onLoading() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void onRequestSuccessful(List<SaleResponse> responseData) {
        bi.refreshMysales.setRefreshing(false);
        mSaleAdapter.getItemList().clear();
        mSaleAdapter.getItemList().addAll(responseData);
        mSaleAdapter.notifyDataSetChanged();
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
    }

    @Override
    public void onRequestFail(String errorMessage) {
        bi.refreshMysales.setRefreshing(false);
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void onRequestNotFound() {
        bi.refreshMysales.setRefreshing(false);
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
    }

    @Override
    public Unit invoke(View view, SaleResponse saleResponse, Integer integer, Map<Integer, ? extends View> integerMap) {
        MySaleItemBinding binding = DataBindingUtil.bind(view);
        binding.setSaleItem(saleResponse);
        binding.executePendingBindings();

        return null;
    }

    @Override
    public Unit invoke(SaleResponse saleResponse, Integer integer) {
        Intent intent = new Intent(MySaleActivity.this, SaleDetailActivity.class);
        intent.putExtra(AppConstants.KEYS.SALE_ITEM.name(), Parcels.wrap(saleResponse));
        startActivity(intent);
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        selectMenuOption(R.id.nav_my_sale);
        if(isChanged) {
            presenter.getSales(loginResponse.access_token);
            isChanged = false;
        }

    }

    @Override
    public void onBackPressed() {
        if(from_notification){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            super.onBackPressed();
        }
    }
}
