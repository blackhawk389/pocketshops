package com.appabilities.sold.screens.product_detail.view;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.database.SoldDatabase;
import com.appabilities.sold.database.tables.CartItemModel;
import com.appabilities.sold.database.tables.CartItemModel_Table;
import com.appabilities.sold.databinding.ActivityProductDetailBinding;
import com.appabilities.sold.model.UserModel_Table;
import com.appabilities.sold.networking.response.ImgResponse;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.networking.response.VerifyUserResponse;
import com.appabilities.sold.screens.add_detail_advertisement.view.AddDetailAdvertActivity;
import com.appabilities.sold.screens.advertisement.view.AdvertisementActivity;
import com.appabilities.sold.screens.chat_with_seller.chat_activity.view.ChatWithSellerActivity;
import com.appabilities.sold.screens.place_bid.view.PlaceBidActivity;
import com.appabilities.sold.screens.product_detail.ProductDetailContract;
import com.appabilities.sold.screens.product_detail.ProductDetailPresenter;
import com.appabilities.sold.screens.seller_profile.view.SellerProfileActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.dk.animation.circle.CircleAnimationUtil;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import me.himanshusoni.quantityview.QuantityView;

public class ProductDetailActivity extends BaseActivity implements ProductDetailContract.View, OnLikeListener, BaseSliderView.OnSliderClickListener {

    ActivityProductDetailBinding bi;
    ProductDetailPresenter presenter;
    ProductResponse productResponse;
    Handler handler;
    ArrayList<CartItemModel> product;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_product_detail);
        bi.setView(this);
        presenter = new ProductDetailPresenter(this);
        bi.setPresenter(presenter);
        supportPostponeEnterTransition();
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        setSupportActionBar(bi.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        productResponse = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.PRODUCT_ITEM.name()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (getIntent().hasExtra(AppConstants.KEYS.TRANSITION_VIEW.name())){
                String imageTransitionName = getIntent().getExtras().getString(AppConstants.KEYS.TRANSITION_VIEW.name());
                bi.imgPhotoProductdetails.setTransitionName(imageTransitionName);
            }
        }

        Picasso.with(this)
                .load(productResponse.imgName)
                .noFade()
                .into(bi.imgPhotoProductdetails, new Callback() {
                    @Override
                    public void onSuccess() {
                        supportStartPostponedEnterTransition();
                        updateSlider();
                    }

                    @Override
                    public void onError() {
                        supportStartPostponedEnterTransition();
                    }
                });
        product = (ArrayList<CartItemModel>) SQLite.select().from(CartItemModel.class).where(CartItemModel_Table.productId.eq(productResponse.productID)).queryList();
        bi.setProductDetail(productResponse);
        bi.ratingProductProductdetails.setRating(Float.valueOf(productResponse.ratings!= null?productResponse.ratings:"0.0"));
        if (productResponse.liked != null && productResponse.liked.equals("1")){
            bi.btnLikeProductdetails.setLiked(true);
        }
        bi.btnLikeProductdetails.setOnLikeListener(this);
        if (productResponse.auctionable.equals("1")){
            bi.layoutBidProductdetails.setVisibility(View.VISIBLE);
            bi.layoutBuy.setVisibility(View.GONE);
            bi.slantedAuctionableProductdetails.setVisibility(View.VISIBLE);
        }

        bi.btnPlaceBidProductdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBid();
            }
        });

        bi.btnBuyProductdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product = (ArrayList<CartItemModel>) SQLite.select().from(CartItemModel.class).where(CartItemModel_Table.productId.eq(productResponse.productID)).queryList();

                int count = 0;
                for(int i = 0; i < product.size(); i++){
                    count = count + product.get(i).quantity;
                }
                if((product != null && Integer.parseInt(productResponse.quantity) >= count + 1)){
                    onClickBuy();
                }else{
                    Toast.makeText(getApplicationContext(), "you cant add products more than available quantity", Toast.LENGTH_SHORT).show();
                }

            }
        });

//        bi.btnChatProductitem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClickChat();
//            }
//        });

        bi.btnChatProductitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickChat();
            }
        });

    }

    @Override
    public void onClickBid() {
        Intent intent = new Intent(ProductDetailActivity.this, PlaceBidActivity.class);
        intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
        startActivity(intent);
    }

    @Override
    public void onClickBuy() {
        View dialogview = (View) this.getLayoutInflater().inflate(R.layout.quantity_product_layout, null);
        final QuantityView qv = (QuantityView) dialogview.findViewById(R.id.quantityView_default);
        qv.setMaxQuantity(Integer.parseInt(productResponse.quantity));



        new AlertDialog.Builder(ProductDetailActivity.this)
               .setTitle("Enter Quantity")
                .setView(dialogview)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add To Cart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new CircleAnimationUtil()
                                .attachActivity(ProductDetailActivity.this)
                                .setTargetView(bi.imgPhotoProductdetails)
                                .setDestView(cartMenuItem.getActionView())
                                .setMoveDuration(700)
                                .setAnimationListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {

                                        // Saving Item in DB
                                        CartItemModel cartItemModel = new CartItemModel(productResponse, qv.getQuantity());
                                        cartItemModel.save();

                                        ProductDetailActivity.this.refreshCount();

                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                })
                                .startAnimation();
                    }
                })
                .show();


//        new MaterialDialog.Builder(this)
//                .title("Enter Quantity")
//                .customView(dialogview, true)
//                .positiveText("Add To Cart")
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//
//                        final QuantityView quantityView = (QuantityView) dialog.findViewById(R.id.quantityView_default);
//                        new CircleAnimationUtil()
//                                .attachActivity(ProductDetailActivity.this)
//                                .setTargetView(bi.imgPhotoProductdetails)
//                                .setDestView(cartMenuItem.getActionView())
//                                .setMoveDuration(700)
//                                .setAnimationListener(new Animator.AnimatorListener() {
//                                    @Override
//                                    public void onAnimationStart(Animator animator) {
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationEnd(Animator animator) {
//
//                                        // Saving Item in DB
//                                        CartItemModel cartItemModel = new CartItemModel(productResponse, quantityView.getQuantity());
//                                        cartItemModel.save();
//
//                                        ProductDetailActivity.this.refreshCount();
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationCancel(Animator animator) {
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationRepeat(Animator animator) {
//
//                                    }
//                                })
//                                .startAnimation();
//                    }
//                })
//                .negativeText("Cancel")
//                .show();

    }

    @Override
    public void onClickChat() {
        Intent intent = new Intent(this, ChatWithSellerActivity.class);
        intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
        startActivity(intent);
    }

    @Override
    public void updateSlider() {
        bi.sliderPhotosProductdetails.removeAllSliders();

        if (productResponse.imgNames != null){
            for (int i = 0; i < productResponse.imgNames.size(); i++) {
                TextSliderView textSliderView = new TextSliderView(ProductDetailActivity.this);
                // initialize a SliderLayout
                textSliderView
                        .image(productResponse.imgNames.get(i).imgName)
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(this);
                bi.sliderPhotosProductdetails.addSlider(textSliderView);
            }
        }
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bi.sliderPhotosProductdetails.setVisibility(View.VISIBLE);
                bi.imgPhotoProductdetails.setVisibility(View.GONE);
            }
        }, 800);

    }

    @Override
    public void updateProductLikeStatus(VerifyUserResponse body, int status) {
        if (status == 1)
            SnackUtils.showSnackMessage(this, "Liked");
        else
            SnackUtils.showSnackMessage(this, "UnLiked");
    }

    @Override
    public void errorUpdateLikeStatus() {

    }

    @Override
    public void onClickSellerProfile() {
        Intent intent = new Intent(this, SellerProfileActivity.class);
        //Intent intent = new Intent(this, TestActivity.class);
        intent.putExtra(AppConstants.KEYS.SELLER_ID.name(), productResponse.userID);
        startActivity(intent);
    }

    @Override
    public void liked(LikeButton likeButton) {
        presenter.onUpdateProductLike(loginResponse.access_token, productResponse.productID, 1);
    }

    @Override
    public void unLiked(LikeButton likeButton) {
        presenter.onUpdateProductLike(loginResponse.access_token, productResponse.productID, 0);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        new ImageViewer.Builder<>(this, productResponse.imgNames)
                .setFormatter(new ImageViewer.Formatter<ImgResponse>() {
                    @Override
                    public String format(ImgResponse customImage) {
                        return customImage.imgName;
                    }
                })
                .show();
    }
}
