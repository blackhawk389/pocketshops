package com.appabilities.sold.screens.offer.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.database.tables.CartItemModel;
import com.appabilities.sold.databinding.ActivityOfferBinding;
import com.appabilities.sold.databinding.OfferItemLayoutBinding;
import com.appabilities.sold.networking.response.ImgResponse;
import com.appabilities.sold.networking.response.OfferResponse;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.screens.chat_with_seller.chat_activity.view.ChatWithSellerActivity;
import com.appabilities.sold.screens.offer.OfferContract;
import com.appabilities.sold.screens.offer.OfferPresenter;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.kennyc.view.MultiStateView;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function4;

public class OfferActivity extends BaseActivity implements OfferContract.View<List<OfferResponse>>, Function4<View, OfferResponse, Integer, Map<Integer, ? extends View>, Unit> {

    OfferPresenter presenter;
    ActivityOfferBinding bi;
    RecyclerAdapterUtil<OfferResponse> mOfferAdapter;
    String productID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_offer);
        bi.setView(this);
        presenter = new OfferPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("OFFERS");

        productID = getIntent().getStringExtra(AppConstants.KEYS.PRODUCT_ID.name());

        mOfferAdapter = new RecyclerAdapterUtil<>(
                this, new ArrayList<OfferResponse>(), R.layout.offer_item_layout);
        mOfferAdapter.addOnDataBindListener(this);
        bi.offerRecycler.setHasFixedSize(true);
        bi.offerRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        bi.offerRecycler.setAdapter(mOfferAdapter);

        presenter.getOffers(loginResponse.access_token, productID);
    }

    @Override
    public void onLoading() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void onRequestSuccessful(List<OfferResponse> responseData) {
        mOfferAdapter.getItemList().addAll(responseData);
        mOfferAdapter.notifyDataSetChanged();
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
    public void offerAcceptedSuccessfully(OfferResponse offerResponse) {

        ProductResponse productResponse = new ProductResponse();
        productResponse.productID = offerResponse.productID;
        productResponse.productTitle = offerResponse.title;
        productResponse.productOwner = offerResponse.sellerName;
        productResponse.quantity = offerResponse.quantity;
        productResponse.imgNames = offerResponse.imgNames;
        productResponse.quantity = offerResponse.quantity;
        productResponse.price = offerResponse.price;
        if (offerResponse.imgNames != null && offerResponse.imgNames.size() > 0){
            productResponse.imgName = offerResponse.imgNames.get(0).imgName;
        }

        CartItemModel cartItemModel = new CartItemModel(productResponse, Integer.parseInt(offerResponse.quantity));
        cartItemModel.save();
        OfferActivity.this.refreshCount();
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },300);*/
        mOfferAdapter.getItemList().remove(offerResponse);
        mOfferAdapter.notifyDataSetChanged();
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        SnackUtils.showSnackMessage(OfferActivity.this, "Offer Accepted Successfully, Place order from cart");
    }

    @Override
    public void errorInAcceptingOffer() {
        SnackUtils.showSnackMessage(OfferActivity.this, "Error!!!");
    }

    @Override
    public Unit invoke(View view, final OfferResponse offerResponse, Integer integer, Map<Integer, ? extends View> integerMap) {
        OfferItemLayoutBinding binding = DataBindingUtil.bind(view);
        binding.setOfferItem(offerResponse);
        binding.executePendingBindings();
        if (offerResponse.subCategoryName != null)
            binding.txtCategory.setText(offerResponse.categoryName +"->"+offerResponse.subCategoryName);
        else
            binding.txtCategory.setText(offerResponse.categoryName);
        binding.txtTime.setText(AppConstants.dateDiff(offerResponse.createdOn));
        if(offerResponse.imgNames != null){
            binding.imgProduct.setImageURI(offerResponse.imgNames.get(0).imgName);
        }
        binding.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OfferActivity.this, ChatWithSellerActivity.class);
                intent.putExtra(AppConstants.KEYS.OFFER_ITEM.name(), Parcels.wrap(offerResponse));
                startActivity(intent);
            }
        });
        binding.btnAcceptOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //presenter.acceptOffer(loginResponse.access_token, offerResponse.productID, offerResponse.offerID);
                presenter.acceptOffer(loginResponse.access_token, offerResponse);
            }
        });

        binding.layoutRootBiditem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(offerResponse.imgNames != null){
                    new ImageViewer.Builder<>(OfferActivity.this, offerResponse.imgNames)
                            .setFormatter(new ImageViewer.Formatter<ImgResponse>() {
                                @Override
                                public String format(ImgResponse customImage) {
                                    return customImage.imgName;
                                }
                            })
                            .show();
                }
            }
        });
        return null;
    }

    public void refreshCount()
    {
        cartItemsCount = (int) SQLite.select().from(CartItemModel.class).queryList().size();
        ActionItemBadge.update(this, cartMenuItem, FontAwesome.Icon.faw_shopping_cart, ActionItemBadge.BadgeStyles.RED.getStyle(), cartItemsCount);

    }
}
