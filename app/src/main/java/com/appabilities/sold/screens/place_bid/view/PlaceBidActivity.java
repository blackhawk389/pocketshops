package com.appabilities.sold.screens.place_bid.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivityPlaceBidBinding;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.screens.auction.view.MyAuctionActivity;
import com.appabilities.sold.screens.home.view.HomeActivity;
import com.appabilities.sold.screens.place_bid.PlaceBidContract;
import com.appabilities.sold.screens.place_bid.PlaceBidPresenter;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.appabilities.sold.utils.TextValidatiors;

import org.parceler.Parcels;

public class PlaceBidActivity extends BaseActivity implements PlaceBidContract.View {

    ActivityPlaceBidBinding bi;
    PlaceBidPresenter presenter;
    ProductResponse productResponse;
    boolean isAuctionExpired = false;
    AppConstants.AUCTION_STATUS auctionStatus = AppConstants.AUCTION_STATUS.OPEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_place_bid);
        bi.setView(this);
        presenter = new PlaceBidPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("PLACE BID");

        productResponse = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.PRODUCT_ITEM.name()));
        bi.txtStartingBidPlacebid.setText("SAR " + productResponse.startingBid);
        bi.txtHighestBidPlacebid.setText("SAR " + productResponse.highestBid);
        bi.txtExpirtyDatePlacebid.setText(productResponse.auctionExpDate);

        auctionStatus = AppConstants.getAuctionStatus(productResponse);
        if (auctionStatus == AppConstants.AUCTION_STATUS.EXPIRED)
        {
            // Expired
            isAuctionExpired = true;


            bi.txtAuctionStatusPlacebid.setText("EXPIRED");
            //txtExpiryDate.setTextSize(getResources().getDimension(R.dimen._13sdp));
            bi.txtAuctionStatusPlacebid.setTextColor(getResources().getColor(R.color.colorAuctionExpired));

            bi.txtBidPriceBidprice.setVisibility(View.GONE);
            bi.txtBidPriceLabelBidprice.setVisibility(View.GONE);
            bi.btnBidPlacebid.setVisibility(View.GONE);
        }
        else if (auctionStatus == AppConstants.AUCTION_STATUS.OPEN)
        {
            // Open
            isAuctionExpired = false;
            bi.txtAuctionStatusPlacebid.setText("OPEN");
            //txtExpiryDate.setTextSize(getResources().getDimension(R.dimen._13sdp));
            bi.txtAuctionStatusPlacebid.setTextColor(getResources().getColor(R.color.colorAuctionOpen));
        }
        else if (auctionStatus == AppConstants.AUCTION_STATUS.AWARDED)
        {
            // Awarded
            bi.txtAuctionStatusPlacebid.setText("COMPLETED");
            bi.txtAuctionStatusPlacebid.setTextColor(getResources().getColor(R.color.colorAuctionApproved));

            bi.txtBidPriceBidprice.setVisibility(View.GONE);
            bi.txtBidPriceLabelBidprice.setVisibility(View.GONE);
            bi.btnBidPlacebid.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClickPlaceBid() {

        if (isAuctionExpired)
        {
            SnackUtils.showSnackMessage(this, "Auction Expired!", true);
            return;
        }

        float minPrice = 0;
        if (Float.valueOf(productResponse.highestBid) == 0){
            minPrice = Float.parseFloat(productResponse.startingBid) + 1;
        }else {
            minPrice = Float.parseFloat(productResponse.highestBid) + 1;
        }


        if (TextValidatiors.isEmpty(bi.txtBidPriceBidprice, "Enter Bid Price!")) return;
        if (TextValidatiors.isGreatThan(bi.txtBidPriceBidprice, "Enter atleast SAR " + minPrice + "", String.valueOf(minPrice), bi.txtBidPriceBidprice.getText().toString())) return;
        bi.btnBidPlacebid.startAnimation();
        presenter.placeBid(loginResponse.access_token, productResponse.productID, bi.txtBidPriceBidprice.getText().toString());
    }

    @Override
    public void successfullyPlaceBid(String msg) {
        bi.btnBidPlacebid.revertAnimation();
        SnackUtils.showSnackMessage(PlaceBidActivity.this, msg);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               startActivity(new Intent(PlaceBidActivity.this, MyAuctionActivity.class));
               finish();
            }
        }, 800);
    }

    @Override
    public void errorInPlaceBid(String error) {
        bi.btnBidPlacebid.revertAnimation();
        SnackUtils.showSnackMessage(PlaceBidActivity.this, error);
    }
}
