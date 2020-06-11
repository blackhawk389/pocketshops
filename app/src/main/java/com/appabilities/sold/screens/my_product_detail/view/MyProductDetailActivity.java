package com.appabilities.sold.screens.my_product_detail.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivityMyProductDetailBinding;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.ImgResponse;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.screens.add_product.view.AddProductActivity;
import com.appabilities.sold.screens.auction_bid.view.AuctionBidActivity;
import com.appabilities.sold.screens.my_product_detail.MyProductDetailContract;
import com.appabilities.sold.screens.my_product_detail.MyProductDetailPresenter;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.iamhabib.easy_preference.EasyPreference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import org.parceler.Parcels;

public class MyProductDetailActivity extends BaseActivity implements MyProductDetailContract.View, BaseSliderView.OnSliderClickListener {

    ActivityMyProductDetailBinding bi;
    MyProductDetailPresenter presenter;
    ProductResponse productResponse;
    Handler handler;
    //MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_my_product_detail);
        bi.setView(this);
        presenter = new MyProductDetailPresenter(this);
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
        bi.setProductDetail(productResponse);
        bi.ratingProductProductdetails.setRating(Float.valueOf(productResponse.ratings!= null?productResponse.ratings:"0.0"));

        if (productResponse.auctionable.equals("1")){
            bi.layoutBidProductdetails.setVisibility(View.VISIBLE);
            bi.slantedAuctionableProductdetails.setVisibility(View.VISIBLE);
        }else {
            bi.layoutBidProductdetails.setVisibility(View.GONE);
            bi.slantedAuctionableProductdetails.setVisibility(View.GONE);
        }

        bi.btnDeleteProductdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDelete();
            }
        });

        bi.btnEditProductdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEdit();
            }
        });

        bi.btnPlaceBidProductdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBids();
            }
        });

        bi.btnShareProductitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShare();
            }
        });
    }

    @Override
    public void updateSlider() {
        bi.sliderPhotosProductdetails.removeAllSliders();

        if (productResponse.imgNames != null){
            for (int i = 0; i < productResponse.imgNames.size(); i++) {
                TextSliderView textSliderView = new TextSliderView(MyProductDetailActivity.this);
                // initialize a SliderLayout
                textSliderView
                        .image(productResponse.imgNames.get(i).imgName)
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(this);
                bi.sliderPhotosProductdetails.addSlider(textSliderView);
            }
        }
        bi.sliderPhotosProductdetails.setVisibility(View.VISIBLE);
        bi.imgPhotoProductdetails.setVisibility(View.GONE);
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
    public void onClickShare() {

        Intent i=new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
//        i.putExtra(android.content.Intent.EXTRA_SUBJECT,"Subject test");
        i.putExtra(android.content.Intent.EXTRA_TEXT, "https://pocket-shops.com/product-detail.php?id=" + productResponse.productID);
        startActivity(Intent.createChooser(i,"Share via"));

//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://pocket-shops.com/product-detail.php?id=" + productResponse.productID));
//        startActivity(browserIntent);
    }

    @Override
    public void onClickEdit() {
        Intent intent = new Intent(MyProductDetailActivity.this, AddProductActivity.class);
        intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
        startActivity(intent);
    }

    @Override
    public void onClickBids() {
        Intent intent = new Intent(MyProductDetailActivity.this, AuctionBidActivity.class);
        intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
        startActivity(intent);
    }

    @Override
    public void onClickDelete() {

        new AlertDialog.Builder(MyProductDetailActivity.this)
                .setTitle("Delete Product?")
                .setMessage("This action is irreversible. Are you sure you want to delete this product?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SnackUtils.showLoadingSnack(MyProductDetailActivity.this, "Deleting Product");
                        presenter.deleteProduct(loginResponse.access_token, productResponse.productID);
                        dialog.dismiss();
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

//        dialog = new MaterialDialog.Builder(this)
//                .title("Delete Product?")
//                .content(" This action is irreversible. Are you sure you want to delete this product?")
//                .positiveText("Delete")
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//
//                        SnackUtils.showLoadingSnack(MyProductDetailActivity.this, "Deleting Product");
//                        presenter.deleteProduct(loginResponse.access_token, productResponse.productID);
//                        dialog.dismiss();
//                    }
//                })
//                .negativeText("Cancel")
//                .onNegative(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        dialog.dismiss();
//                    }
//                })
//                .show();
    }

    @Override
    public void errorInDeletingProduct() {
        SnackUtils.hideLoadingSnack();
        SnackUtils.showSnackMessage(MyProductDetailActivity.this, "Error In Deleting Product",false);
    }

    @Override
    public void productDeletedSuccessfully(BaseResponse body) {
        SnackUtils.hideLoadingSnack();
        SnackUtils.showSnackMessage(MyProductDetailActivity.this, body.msg,true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EasyPreference.with(MyProductDetailActivity.this).addBoolean(AppConstants.KEYS.IS_REFRESH.name(), true).save();
                finish();
            }
        }, 300);
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
