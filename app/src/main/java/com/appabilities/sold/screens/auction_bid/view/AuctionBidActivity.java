package com.appabilities.sold.screens.auction_bid.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivityAuctionBidBinding;
import com.appabilities.sold.databinding.BidItemLayoutBinding;
import com.appabilities.sold.databinding.BidderItemLayoutBinding;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.BidItemResponse;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.networking.response.SellerProfileResponse;
import com.appabilities.sold.screens.auction_bid.AuctionBidContract;
import com.appabilities.sold.screens.auction_bid.AuctionBidPresenter;
import com.appabilities.sold.screens.chat_with_seller.chat_activity.view.ChatWithSellerActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.kennyc.view.MultiStateView;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function4;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

public class AuctionBidActivity extends BaseActivity implements
        AuctionBidContract.View<List<BidItemResponse>>, Function4<View, BidItemResponse, Integer, Map<Integer, ? extends View>, Unit> {

    ActivityAuctionBidBinding bi;
    AuctionBidPresenter presenter;
    RecyclerAdapterUtil<BidItemResponse> mBidAdapter;
    ProductResponse mProductItem;
    public AppConstants.AUCTION_STATUS auctionStatus = AppConstants.AUCTION_STATUS.OPEN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_auction_bid);
        bi.setView(this);
        presenter = new AuctionBidPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        setSupportActionBar(bi.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("BID LIST");

        changeStatusBarColor();
        bi.toolbar.setTitle("BID LIST");
        bi.tvTitle.setText("BID LIST");
        mBidAdapter = new RecyclerAdapterUtil<>(this, new ArrayList<BidItemResponse>(), R.layout.bidder_item_layout);
        mBidAdapter.addOnDataBindListener(this);
        bi.recylcerBidsAuction.setHasFixedSize(true);
        bi.recylcerBidsAuction.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        bi.recylcerBidsAuction.setAdapter(mBidAdapter);

        mProductItem = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.PRODUCT_ITEM.name()));
        bi.setProductItem(mProductItem);
        bi.executePendingBindings();
        setupAuctionStatus(AppConstants.getAuctionStatus(mProductItem));
        presenter.getBidList(loginResponse.access_token, mProductItem.productID);
    }

    @Override
    public void onLoading() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void onRequestSuccessful(List<BidItemResponse> responseData) {
        bi.txtTotalBidsAuction.setText(String.valueOf(responseData.size()));
        mBidAdapter.getItemList().removeAll(mBidAdapter.getItemList());
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
    public void setupAuctionStatus(AppConstants.AUCTION_STATUS status) {
        auctionStatus = status;

        if (auctionStatus == AppConstants.AUCTION_STATUS.EXPIRED)
        {
            // Expired
            bi.txtAuctionStatusAuction.setText("AUCTION EXPIRED!");
            bi.txtAuctionStatusAuction.setBackgroundColor(getResources().getColor(R.color.colorAuctionExpired));
            auctionStatus = AppConstants.AUCTION_STATUS.EXPIRED;
        }
        else if (auctionStatus == AppConstants.AUCTION_STATUS.OPEN)
        {
            // Open
            bi.txtAuctionStatusAuction.setText("OPEN FOR BIDS");
            bi.txtAuctionStatusAuction.setBackgroundColor(getResources().getColor(R.color.colorAuctionOpen));
            auctionStatus = AppConstants.AUCTION_STATUS.OPEN;
        }
        else if (auctionStatus == AppConstants.AUCTION_STATUS.AWARDED)
        {
            // Awarded
            bi.txtAuctionStatusAuction.setText("COMPLETED");
            bi.txtAuctionStatusAuction.setBackgroundColor(getResources().getColor(R.color.colorAuctionApproved));
            auctionStatus = AppConstants.AUCTION_STATUS.AWARDED;
        }
    }

    @Override
    public Unit invoke(View view, final BidItemResponse bidItemResponse, Integer integer, Map<Integer, ? extends View> integerMap) {
        final BidderItemLayoutBinding binding = DataBindingUtil.bind(view);
        binding.setBidItem(bidItemResponse);
        binding.executePendingBindings();

        if (bidItemResponse.approved.equals("1")){
            binding.txtStatus.setVisibility(View.GONE);
            binding.txtBidStatusBiditem.setVisibility(View.VISIBLE);
        }else if (bidItemResponse.approved.equals("0")){
            binding.txtStatus.setText("AWARD");
            binding.txtBidStatusBiditem.setVisibility(View.GONE);
        }



        binding.txtStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auctionStatus != AppConstants.AUCTION_STATUS.OPEN){
                    SnackUtils.showSnackMessage(AuctionBidActivity.this, "Product Already Awarded");
                    return;
                }

                Call<BaseResponse> callUpdateStatus = NetController.getInstance().getProductService().awardBid(
                        bidItemResponse.bidID, loginResponse.access_token
                );
                callUpdateStatus.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.code() == HTTP_OK){
                            setupAuctionStatus(AppConstants.AUCTION_STATUS.AWARDED);
                            SnackUtils.showSnackMessage(AuctionBidActivity.this, "Product Awarded Successfully");
                            binding.txtStatus.setVisibility(View.GONE);
                            binding.txtBidStatusBiditem.setVisibility(View.VISIBLE);
                            presenter.getBidList(loginResponse.access_token, mProductItem.productID);
                        }else {
                            SnackUtils.showSnackMessage(AuctionBidActivity.this, "Error");
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {

                    }
                });
            }
        });

        binding.txtChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AuctionBidActivity.this, ChatWithSellerActivity.class);
                intent.putExtra(AppConstants.KEYS.BIDDER.name(), Parcels.wrap(bidItemResponse));
                startActivity(intent);
            }
        });

        return null;
    }
}
