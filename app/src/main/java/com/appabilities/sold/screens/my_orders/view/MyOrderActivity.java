package com.appabilities.sold.screens.my_orders.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.appabilities.sold.R;
import com.appabilities.sold.base.sidemenu.SideMenuBaseActivity;
import com.appabilities.sold.databinding.ActivityMyOrderBinding;
import com.appabilities.sold.databinding.MyOrderItemBinding;
import com.appabilities.sold.networking.response.OrderResponse;
import com.appabilities.sold.screens.home.view.HomeActivity;
import com.appabilities.sold.screens.my_order_detail.view.MyOrderDetailActivity;
import com.appabilities.sold.screens.my_orders.MyOrderContract;
import com.appabilities.sold.screens.my_orders.MyOrderPresenter;
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

public class MyOrderActivity extends SideMenuBaseActivity implements
        MyOrderContract.View<List<OrderResponse>>, Function4<View, OrderResponse, Integer, Map<Integer, ? extends View>, Unit>, Function2<OrderResponse, Integer, Unit> {

    ActivityMyOrderBinding bi;
    MyOrderPresenter presenter;
    RecyclerAdapterUtil<OrderResponse> mOrderAdapter;
    List<OrderResponse> orderList;
    boolean isRefresh = false;
    boolean from_notification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = (ActivityMyOrderBinding) setContentLayout(R.layout.activity_my_order);
        presenter = new MyOrderPresenter(this);
        bi.setView(this);
        bi.setPresenter(presenter);

        if(getIntent().getExtras() != null){
            from_notification = getIntent().getExtras().getBoolean("from_notification");
        }

        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        initDrawer(bi.toolbar, "ORDERS");

        orderList= new ArrayList<>();
        mOrderAdapter = new RecyclerAdapterUtil<>(this, orderList , R.layout.my_order_item);
        mOrderAdapter.addOnDataBindListener(this);
        mOrderAdapter.addOnClickListener(this);
        bi.recyclerOrders.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        bi.recyclerOrders.setLayoutManager(layoutManager);
        bi.recyclerOrders.setAdapter(mOrderAdapter);

        if (getIntent().hasExtra(AppConstants.KEYS.IS_REFRESH.name())){
            isRefresh = getIntent().getBooleanExtra(AppConstants.KEYS.IS_REFRESH.name(),false);
        }

        presenter.getOrders(loginResponse.access_token);
        bi.refreshMyOrder.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getOrders(loginResponse.access_token);
            }
        });
    }

    @Override
    public void onLoading() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void onRequestSuccessful(List<OrderResponse> responseData) {
        bi.refreshMyOrder.setRefreshing(false);
        orderList.clear();
        orderList.addAll(responseData);
        mOrderAdapter.notifyDataSetChanged();
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
    }

    @Override
    public void onRequestFail(String errorMessage) {
        bi.refreshMyOrder.setRefreshing(false);
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void onRequestNotFound() {
        bi.refreshMyOrder.setRefreshing(false);
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
    }

    @Override
    public Unit invoke(View view, OrderResponse orderResponse, Integer integer, Map<Integer, ? extends View> integerMap) {
        MyOrderItemBinding binding = DataBindingUtil.bind(view);
        binding.setOrderItem(orderResponse);
        binding.executePendingBindings();
        return null;
    }

    @Override
    public Unit invoke(OrderResponse orderResponse, Integer integer) {
        Intent intent = new Intent(this, MyOrderDetailActivity.class);
        intent.putExtra(AppConstants.KEYS.ORDER_ITEM.name(), Parcels.wrap(orderResponse));
        startActivity(intent);
        return null;
    }

    @Override
    public void supportFinishAfterTransition() {
        if (isRefresh){
            startActivity(new Intent(MyOrderActivity.this, HomeActivity.class));
            finish();
        }else {
            super.supportFinishAfterTransition();
        }
    }

    @Override
    public void onBackPressed() {
        if (isRefresh){
            startActivity(new Intent(MyOrderActivity.this, HomeActivity.class));
            finish();
        }
        else if(from_notification){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        selectMenuOption(R.id.nav_my_orders);
        if(isChanged) {
            presenter.getOrders(loginResponse.access_token);
            isChanged = false;
        }
    }
}
