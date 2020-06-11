package com.appabilities.sold.screens.request_detail.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivityRequestDetailBinding;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.ImgResponse;
import com.appabilities.sold.networking.response.RequestResponse;
import com.appabilities.sold.screens.add_offer.view.AddOfferActivity;
import com.appabilities.sold.screens.add_request.view.AddRequestActivity;
import com.appabilities.sold.screens.chat_with_seller.chat_activity.view.ChatWithSellerActivity;
import com.appabilities.sold.screens.my_product_detail.view.MyProductDetailActivity;
import com.appabilities.sold.screens.offer.view.OfferActivity;
import com.appabilities.sold.screens.request_detail.RequestDetailContract;
import com.appabilities.sold.screens.request_detail.RequestDetailPresenter;
import com.appabilities.sold.screens.seller_profile.view.SellerProfileActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import org.parceler.Parcels;

public class RequestDetailActivity extends BaseActivity implements RequestDetailContract.View, BaseSliderView.OnSliderClickListener {

    RequestDetailPresenter presenter;
    ActivityRequestDetailBinding bi;
    RequestResponse requestResponse;
    boolean isMyRequest;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_request_detail);
        bi.setView(this);
        presenter = new RequestDetailPresenter(this);
        presenter.initScreen();
        bi.setPresenter(presenter);
    }

    @Override
    public void setupViews() {
        setSupportActionBar(bi.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        requestResponse = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.REQUEST_ITEM.name()));
        isMyRequest = getIntent().getBooleanExtra(AppConstants.KEYS.IS_FROM_MY_REQUEST.name(), false);

        if (isMyRequest){
            bi.layoutRequester.setVisibility(View.VISIBLE);
            bi.layoutOffer.setVisibility(View.GONE);
            bi.layoutUser.setVisibility(View.GONE);
        }else {
            bi.layoutOffer.setVisibility(View.VISIBLE);
            bi.layoutRequester.setVisibility(View.GONE);
            bi.layoutUser.setVisibility(View.VISIBLE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String imageTransitionName = getIntent().getExtras().getString(AppConstants.KEYS.TRANSITION_VIEW.name());
            bi.imgPhotoProductdetails.setTransitionName(imageTransitionName);
        }

        Picasso.with(this)
                .load(requestResponse.imgName)
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

        bi.setRequestItem(requestResponse);
    }

    @Override
    public void updateSlider() {
        bi.sliderPhotosProductdetails.removeAllSliders();

        if (requestResponse.imgNames != null){
            for (int i = 0; i < requestResponse.imgNames.size(); i++) {
                TextSliderView textSliderView = new TextSliderView(RequestDetailActivity.this);
                // initialize a SliderLayout
                textSliderView
                        .image(requestResponse.imgNames.get(i).imgName)
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
    public void onClickMakeOffer() {
        Intent intent = new Intent(RequestDetailActivity.this, AddOfferActivity.class);
        intent.putExtra(AppConstants.KEYS.PRODUCT_ID.name(), requestResponse.productID);
        startActivity(intent);
    }

    @Override
    public void onClickChat() {
        Intent intent = new Intent(this, ChatWithSellerActivity.class);
        intent.putExtra(AppConstants.KEYS.REQUEST_ITEM.name(), Parcels.wrap(requestResponse));
        startActivity(intent);
    }

    @Override
    public void onClickEdit() {
        Intent intent = new Intent(RequestDetailActivity.this, AddRequestActivity.class);
        intent.putExtra(AppConstants.KEYS.REQUEST_ITEM.name(), Parcels.wrap(requestResponse));
        startActivity(intent);
    }

    @Override
    public void onClickViewOffer() {
        Intent intent = new Intent(RequestDetailActivity.this, OfferActivity.class);
        intent.putExtra(AppConstants.KEYS.PRODUCT_ID.name(), requestResponse.productID);
        startActivity(intent);
    }

    @Override
    public void onClickDelete() {

        new AlertDialog.Builder(RequestDetailActivity.this)
                .setTitle("Delete Product?")
                .setMessage(" This action is irreversible. Are you sure you want to delete this product?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SnackUtils.showLoadingSnack(RequestDetailActivity.this, "Deleting Request");
                        dialog.dismiss();
                        presenter.deleteRequest(loginResponse.access_token, requestResponse.productID);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
//        new MaterialDialog.Builder(this)
//                .title("Delete Request?")
//                .content(" This action is irreversible. Are you sure you want to delete this request?")
//                .positiveText("Delete")
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//
//                        SnackUtils.showLoadingSnack(RequestDetailActivity.this, "Deleting Request");
//                        dialog.dismiss();
//                        presenter.deleteRequest(loginResponse.access_token, requestResponse.productID);
//
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
    public void showProfileOnClick() {
        Intent intent = new Intent(RequestDetailActivity.this, SellerProfileActivity.class);
        intent.putExtra(AppConstants.KEYS.SELLER_ID.name(), requestResponse.requesterID);
        startActivity(intent);
    }

    @Override
    public void requestDeletedSuccessfully(BaseResponse body) {
        SnackUtils.hideLoadingSnack();
        SnackUtils.showSnackMessage(RequestDetailActivity.this, body.msg,true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 800);
    }

    @Override
    public void errorInDeletingRequest() {
        SnackUtils.hideLoadingSnack();
        SnackUtils.showSnackMessage(RequestDetailActivity.this, "Error In Deleting Product",false);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        new ImageViewer.Builder<>(this, requestResponse.imgNames)
                .setFormatter(new ImageViewer.Formatter<ImgResponse>() {
                    @Override
                    public String format(ImgResponse customImage) {
                        return customImage.imgName;
                    }
                })
                .show();
    }
}
