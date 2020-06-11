package com.appabilities.sold.screens.add_offer.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivityAddOfferBinding;
import com.appabilities.sold.databinding.HorizontalImageItemLayoutBinding;
import com.appabilities.sold.screens.add_offer.AddOfferContract;
import com.appabilities.sold.screens.add_offer.AddOfferPresenter;
import com.appabilities.sold.screens.add_request.view.AddRequestActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.bumptech.glide.Glide;
import com.kennyc.view.MultiStateView;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;

import java.util.ArrayList;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function4;

public class AddOfferActivity extends BaseActivity implements
        AddOfferContract.View, Function4<View, Image, Integer, Map<Integer, ? extends View>, Unit> {

    ActivityAddOfferBinding bi;
    AddOfferPresenter presenter;
    private ArrayList<Image> mSelectedImages;
    RecyclerAdapterUtil<Image> galleryAdapter;
    String productID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_add_offer);
        bi.setView(this);
        presenter = new AddOfferPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ADD OFFER");

        productID = getIntent().getStringExtra(AppConstants.KEYS.PRODUCT_ID.name());
        mSelectedImages = new ArrayList<>();
        galleryAdapter = new RecyclerAdapterUtil<>(this, mSelectedImages, R.layout.horizontal_image_item_layout);
        galleryAdapter.addOnDataBindListener(this);
        bi.recyclerImages.setHasFixedSize(true);
        bi.recyclerImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        bi.recyclerImages.setAdapter(galleryAdapter);
    }

    @Override
    public void onClickAddOffer() {
        presenter.addOffer(productID,
                bi.edtOfferTitle.getText().toString().trim(),
                bi.edtOfferDesc.getText().toString().trim(),
                bi.edtQuantity.getText().toString().trim(),
                bi.edtPrice.getText().toString().trim(),
                loginResponse.access_token,
                mSelectedImages);
    }

    @Override
    public void onClickGalleryForImages() {
        int selectionLimit = mSelectedImages.size();
        if (selectionLimit == 5) {
            SnackUtils.showSnackMessage(this, "Delete images to select more");
            return;
        }

        ImagePicker.with(this)                         //  Initialize ImagePicker with activity or fragment context
                .setToolbarColor("#f7941d")         //  Toolbar color
                .setStatusBarColor("#ab5d01")       //  StatusBar color (works with SDK >= 21  )
                .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
                .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
                .setProgressBarColor("#4CAF50")     //  ProgressBar color
                .setBackgroundColor("#212121")      //  Background color
                .setCameraOnly(false)               //  Camera mode
                .setMultipleMode(true)              //  Select multiple images or single image
                .setFolderMode(true)                //  Folder mode
                .setShowCamera(true)                //  Show camera button
                .setFolderTitle("Folder")           //  Folder title (works with FolderMode = true)
                .setImageTitle("Galleries")         //  Image title (works with FolderMode = false)
                .setDoneTitle("Done")               //  Done button title
                .setMaxSize(5)                     //  Max images can be selected
                .setSavePath("Camera")         //  Image capture folder name
                .setSelectedImages(mSelectedImages)          //  Selected images
                .start();
    }

    @Override
    public void successfullyAddedOffer() {
        loadContent();
//        bi.fabSaveOffer.setClickable(false);
        SnackUtils.showSnackMessage(this, "Offer added successfully");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 300);

    }

    @Override
    public void errorInAddingOffer() {
        SnackUtils.showSnackMessage(this, "Error in adding offer");
    }

    @Override
    public void enterOfferTitle(String s) {
        bi.edtOfferTitle.setError(s);
        bi.edtOfferTitle.requestFocus();
    }

    @Override
    public void enterOfferDetail(String s) {
        bi.edtOfferDesc.setError(s);
        bi.edtOfferDesc.requestFocus();
    }

    @Override
    public void enterOfferQuantity(String s) {
        bi.edtQuantity.setError(s);
        bi.edtQuantity.requestFocus();
    }

    @Override
    public void enterOfferPrice(String s) {
        bi.edtPrice.setError(s);
        bi.edtPrice.requestFocus();
    }

    @Override
    public void selectImagesForOffer(String s) {
        loadContent();
        SnackUtils.showSnackMessage(this, s);
    }

    @Override
    public void onLoading() {
        bi.multiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void loadContent() {
        bi.multiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            mSelectedImages = images;
        }
        bi.recyclerImages.setVisibility(View.VISIBLE);
        galleryAdapter.getItemList().clear();
        galleryAdapter.getItemList().addAll(mSelectedImages);
        galleryAdapter.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Unit invoke(View view, Image image, final Integer position, Map<Integer, ? extends View> integerMap) {
        final HorizontalImageItemLayoutBinding binding = DataBindingUtil.bind(view);
        View hover = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_product_image_hover, null);
        binding.blurProductItem.setHoverView(hover);
        binding.blurProductItem.setBlurDuration(10);
        binding.blurProductItem.enableZoomBackground(true);
        binding.blurProductItem.dismissHover();
        String img = mSelectedImages.get(position).getPath();
        Glide.with(AddOfferActivity.this).load(img).into(binding.imgSelectedGallery);

        final ImageView imgDelete = (ImageView) hover.findViewById(R.id.imgDelete_gallery);
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgDelete.getVisibility() == View.INVISIBLE)
                    imgDelete.setVisibility(View.VISIBLE);
                binding.blurProductItem.toggleHover();
                imgDelete.setVisibility(View.INVISIBLE);
                mSelectedImages.remove((int) position);
                galleryAdapter.getItemList().remove((int)position);
                //galleryAdapter.getItemList().addAll(mSelectedImages);
                galleryAdapter.notifyDataSetChanged();
            }
        });
        return null;
    }
}
