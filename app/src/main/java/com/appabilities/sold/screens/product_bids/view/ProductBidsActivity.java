package com.appabilities.sold.screens.product_bids.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivityProductBidsBinding;
import com.appabilities.sold.databinding.BidDetailItemLayoutBinding;
import com.appabilities.sold.model.BidModel;
import com.appabilities.sold.model.OrderDetailModel;
import com.appabilities.sold.model.OrderModel;
import com.appabilities.sold.networking.response.UserBidResponse;
import com.appabilities.sold.screens.confirm_order.view.ConfirmOrderActivity;
import com.appabilities.sold.screens.product_bids.ProductBidsContract;
import com.appabilities.sold.screens.product_bids.ProductBidsPresenter;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;

public class ProductBidsActivity extends BaseActivity implements ProductBidsContract.View, Function4<View, BidModel, Integer, Map<Integer, ? extends View>, Unit>, Function2<BidModel, Integer, Unit> {

    ProductBidsPresenter presenter;
    ActivityProductBidsBinding bi;
    RecyclerAdapterUtil<BidModel> mBidAdapter;
    List<BidModel> bidList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_product_bids);
        bi.setView(this);
        presenter = new ProductBidsPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("MY BIDS");
        changeStatusBarColor();

        bidList = new ArrayList<>();
        UserBidResponse userBidResponse =
                Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.BIDS_ITEM.name()));
        for (int i = 0; i < userBidResponse.bidList.size(); i++) {
            BidModel bidModel = new BidModel();
            bidModel.bidID = userBidResponse.bidList.get(i).bidID;
            bidModel.bidPrice = userBidResponse.bidList.get(i).bidPrice;
            bidModel.categoryName = userBidResponse.categoryName;
            bidModel.subCategoryName = userBidResponse.subCategoryName;
            bidModel.description = userBidResponse.productDesc;
            bidModel.ownerName = userBidResponse.productOwner;
            bidModel.productImg = userBidResponse.imgName;
            bidModel.productTitle = userBidResponse.productTitle;
            bidModel.aucted = userBidResponse.bidList.get(i).aucted;
            bidModel.productID = userBidResponse.productID;
            bidModel.bidOrdered = userBidResponse.bidList.get(i).bidOrdered;
            bidModel.productQty = userBidResponse.quantity;
            bidList.add(bidModel);
        }
        mBidAdapter = new RecyclerAdapterUtil<>(this, bidList, R.layout.bid_detail_item_layout);
        mBidAdapter.addOnDataBindListener(this);
        mBidAdapter.addOnClickListener(this);
        bi.recyclerMyBids.setHasFixedSize(true);
        bi.recyclerMyBids.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        bi.recyclerMyBids.setAdapter(mBidAdapter);
    }

    @Override
    public Unit invoke(View view, BidModel bidModel, Integer position, Map<Integer, ? extends View> integerMap) {
        BidDetailItemLayoutBinding binding = DataBindingUtil.bind(view);
        binding.setBidDetailItem(bidModel);
        binding.executePendingBindings();

        if (bidModel.subCategoryName == null)
            binding.txtCategory.setText(bidModel.categoryName);
        else
            binding.txtCategory.setText(bidModel.categoryName+"->"+bidModel.subCategoryName);

        if (bidModel.aucted.equals("1")){
            binding.txtBidStatusBiditem.setVisibility(View.VISIBLE);
            binding.txtStatus.setVisibility(View.GONE);
        }else {
            binding.txtBidStatusBiditem.setVisibility(View.GONE);
            binding.txtStatus.setVisibility(View.VISIBLE);
        }

        return null;
    }

    @Override
    public Unit invoke(BidModel bidModel, Integer position) {
        if (bidModel.aucted.equals("1")){
            if (bidModel.bidOrdered.equals("0")){
                OrderDetailModel orderDetailModel = new OrderDetailModel();
                orderDetailModel.setProductTitle(bidModel.productTitle);
                orderDetailModel.setProductID(bidModel.productID);
                orderDetailModel.setQuantity(bidModel.productQty);
                orderDetailModel.setTotalItemPrice(bidModel.bidPrice);
                orderDetailModel.setItemPrice(bidModel.bidPrice);
                orderDetailModel.setItemURL(bidModel.productImg);
                orderDetailModel.setTaxes("0");
                orderDetailModel.setShipping("0");
                orderDetailModel.setOrder_auction("1");
                orderDetailModel.setBidID(bidModel.bidID);
                List<OrderDetailModel> listOrderDetail = new ArrayList<>();
                listOrderDetail.add(orderDetailModel);
                OrderModel orderModel = new OrderModel(bidModel.bidPrice, listOrderDetail);
                Intent intent = new Intent(ProductBidsActivity.this, ConfirmOrderActivity.class);
                intent.putExtra(AppConstants.KEYS.ORDER_ITEM.name(), Parcels.wrap(orderModel));
                startActivity(intent);
            }else {
                SnackUtils.showSnackMessage(ProductBidsActivity.this, "Order Already Placed");
            }

        }else
            SnackUtils.showSnackMessage(ProductBidsActivity.this, "Product Not Awarded Yet");
        return null;
    }


}
