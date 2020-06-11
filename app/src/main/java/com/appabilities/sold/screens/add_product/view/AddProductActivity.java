package com.appabilities.sold.screens.add_product.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.base.DatePickerFragment;
import com.appabilities.sold.base.sidemenu.SideMenuBaseActivity;
import com.appabilities.sold.custom.chips.Person;
import com.appabilities.sold.database.SoldDatabase;
import com.appabilities.sold.database.tables.ProductImageModel;
import com.appabilities.sold.databinding.ActivityAddProductBinding;
import com.appabilities.sold.databinding.HorizontalImageItemLayoutBinding;
import com.appabilities.sold.model.UserModel;
import com.appabilities.sold.networking.response.CategoriesResponse;
import com.appabilities.sold.networking.response.ImgResponse;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.networking.response.SubCategoryResponse;
import com.appabilities.sold.screens.add_product.AddProductContract;
import com.appabilities.sold.screens.add_product.AddProductPresenter;
import com.appabilities.sold.screens.home.view.HomeActivity;
import com.appabilities.sold.screens.my_products.view.MyProductsActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.bumptech.glide.Glide;
import com.kennyc.view.MultiStateView;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;
import com.tokenautocomplete.TokenCompleteTextView;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;

public class AddProductActivity extends BaseActivity implements AddProductContract.View<List<CategoriesResponse>>, CompoundButton.OnCheckedChangeListener, Function4<View, Image, Integer, Map<Integer, ? extends View>, Unit>, Function2<Image, Integer, Unit>, TokenCompleteTextView.TokenListener<Person> {

    private static final String TAG = AddProductActivity.class.getSimpleName();
    ActivityAddProductBinding bi;
    AddProductPresenter presenter;
    List<String> categoryList;
    List<String> subCategoryList;
    List<CategoriesResponse> categoriesList;
    String categoryID, subCategoryID;
    boolean isAuctionable = false;
    private ArrayList<Image> mSelectedImages;
    List<Person> guestsList;
    private RecyclerAdapterUtil<Image> galleryAdapter;
    private LinearLayoutManager layoutManager;
    private ProductResponse productResponse;
    private boolean isFromEdit = false;
    StringBuilder imageDeleteStringBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_add_product);
        presenter = new AddProductPresenter(this);
        bi.setView(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ADD PRODUCT");
        bi.layoutAuctionableContentNewproduct.setVisibility(View.GONE);
        bi.switchAuctionableNewproduct.setOnCheckedChangeListener(this);
        mSelectedImages = new ArrayList<>();
        galleryAdapter = new RecyclerAdapterUtil<>(this, mSelectedImages, R.layout.horizontal_image_item_layout);
        galleryAdapter.addOnDataBindListener(this);
        galleryAdapter.addOnClickListener(this);

        bi.recyclerMiniGalleryNewproduct.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        bi.recyclerMiniGalleryNewproduct.setLayoutManager(layoutManager);
        bi.recyclerMiniGalleryNewproduct.setAdapter(galleryAdapter);
        presenter.getCategories();
        bi.spinnerCategoryNewproduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categoryID = categoriesList.get(position).categoryID;
                if (categoryID.equals("18")){
                    bi.txtColorNewproduct.setVisibility(View.GONE);
                  //  bi.txtSizeNewproduct.setVisibility(View.GONE);
                }else if (categoryID.equals("2")){
                    bi.txtColorNewproduct.setVisibility(View.VISIBLE);
                    bi.txtSizeNewproduct.setVisibility(View.VISIBLE);
                }else {
                    bi.txtColorNewproduct.setVisibility(View.VISIBLE);
                    //bi.txtSizeNewproduct.setVisibility(View.GONE);
                }
                subCategoryList = new ArrayList<String>();
                if (categoriesList.get(position).subcategories != null) {
                    for (SubCategoryResponse item :
                            categoriesList.get(position).subcategories) {
                        subCategoryList.add(item.categoryName);
                    }
                    updateSubCategories();
                } else {
                    bi.spinnerSubCategoryNewproduct.setEnabled(false);
                    bi.spinnerSubCategoryNewproduct.setText("");
                    bi.spinnerSubCategoryNewproduct.setHint("None");
                    subCategoryID = "";
                    bi.btnSubCategoryDropDown.setClickable(false);
                }
            }
        });

        bi.spinnerSubCategoryNewproduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (CategoriesResponse item :
                        categoriesList) {
                    if (item.categoryID.equals(categoryID)) {
                        subCategoryID = item.subcategories.get(position).categoryID;
                        return;
                    }
                }
            }
        });


        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        bi.txtAuctionExpiryDateNewproduct.setText(year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", (day)));

        guestsList = new ArrayList<>();
        bi.recipientGuestsListNewNewproduct.setSplitChar(new char[]{',', ';', ' '});
        bi.recipientGuestsListNewNewproduct.setTokenLimit(10);
        bi.recipientGuestsListNewNewproduct.setTokenListener(this);
        bi.spinnerCategoryNewproduct.setThreshold(0);
        bi.spinnerSubCategoryNewproduct.setThreshold(0);
        List<UserModel> userList = SoldDatabase.getFollowersList(loginResponse.userID);
        for (int i = 0; i < userList.size(); i++) {
            bi.recipientGuestsListNewNewproduct.addObject(new Person(userList.get(i).display_name, userList.get(i).email));
        }
    }

    @Override
    public void onLoading() {
        bi.multiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void onRequestSuccessful(List<CategoriesResponse> responseData) {
        categoriesList = responseData;
        categoryList = new ArrayList<>();
        for (CategoriesResponse item :
                categoriesList) {
            categoryList.add(item.categoryName);
        }
        bi.spinnerCategoryNewproduct.setAdapter(
                new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, categoryList));

        if (getIntent().hasExtra(AppConstants.KEYS.PRODUCT_ITEM.name())) {
            productResponse = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.PRODUCT_ITEM.name()));
            updateProductDetails();
            isFromEdit = true;
            getSupportActionBar().setTitle("EDIT PRODUCT");
            imageDeleteStringBuilder = new StringBuilder();
            if (getIntent().hasExtra(AppConstants.KEYS.IS_FROM_AUCTION.name())){
                bi.switchAuctionableNewproduct.setChecked(true);
                getSupportActionBar().setTitle("AUCTION PRODUCT");
            }
        }

        bi.multiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
    }

    @Override
    public void onRequestFail(String errorMessage) {
        bi.multiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void onRequestNotFound() {
        bi.multiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
    }

    @Override
    public void onClickAddProduct() {
        if (categoryID == null || categoryID.equals("")) {
            SnackUtils.showSnackMessage(this, "Select Category To Add Product");
            return;
        }
        for (CategoriesResponse item :
                categoriesList) {
            if (categoryID.equals(item.categoryID)) {
                if (item.subcategories != null && item.subcategories.size() > 0) {
                    if (subCategoryID == null || subCategoryID.equals("")) {
                        SnackUtils.showSnackMessage(this, "Select Subcategory To Add Product");
                        return;
                    }
                }
            }
        }
        if (!isFromEdit) {
            if (!isAuctionable) {
                presenter.addProduct(loginResponse.access_token,
                        bi.txtProductTitleNewproduct.getText().toString().trim(),
                        bi.txtDetailsNewproduct.getText().toString().trim(),
                        categoryID,
                        subCategoryID,
                        bi.txtPriceNewproduct.getText().toString().trim(),
                        bi.txtQuantityNewproduct.getText().toString().trim(),
                        mSelectedImages,
                        bi.txtColorNewproduct.getText().toString().trim(),
                        bi.txtSizeNewproduct.getText().toString().trim());
            } else if (isAuctionable) {
                String guestListStr = "";
                if (guestsList.size() > 0) {
                    for (int i = 0; i < guestsList.size(); i++) {
                        if(i == guestsList.size() -1){
                            guestListStr += guestsList.get(guestsList.size() - 1).getEmail();
                        }
                        else {
                            guestListStr += guestsList.get(i).getEmail() + ", ";
                        }
                    }
                }
                presenter.addAuctionProduct(loginResponse.access_token,
                        bi.txtProductTitleNewproduct.getText().toString().trim(),
                        bi.txtDetailsNewproduct.getText().toString().trim(),
                        categoryID,
                        subCategoryID,
                        bi.txtPriceNewproduct.getText().toString().trim(),
                        bi.txtQuantityNewproduct.getText().toString().trim(),
                        mSelectedImages,
                        bi.txtStartingBidPriceNewproduct.getText().toString().trim(),
                        bi.txtAuctionExpiryDateNewproduct.getText().toString().trim(),
                        guestListStr,
                        bi.txtColorNewproduct.getText().toString().trim(),
                        bi.txtSizeNewproduct.getText().toString().trim());
            }
        } else {
            String guestListStr = "";
            if (guestsList.size() > 0) {
                for (int i = 0; i < guestsList.size(); i++) {
                    if(i == guestsList.size() -1){
                        guestListStr += guestsList.get(guestsList.size() - 1).getEmail();
                    }
                    else {
                        guestListStr += guestsList.get(i).getEmail() + ", ";
                    }
                }
            }
            String imagesToDeleteID = "";
            if (imageDeleteStringBuilder != null && imageDeleteStringBuilder.length() > 1)
                imagesToDeleteID = imageDeleteStringBuilder.substring(0, imageDeleteStringBuilder.length() - 1);
            List<Image> listUpdatedImages = new ArrayList<>();
            for (int i = 0; i < mSelectedImages.size(); i++) {
                if (!mSelectedImages.get(i).getPath().equals(productResponse.imgNames.get(i).imgName)){
                    listUpdatedImages.add(mSelectedImages.get(i));
                }
            }
            presenter.updateProduct(isAuctionable == true ? "1" : "0",
                    loginResponse.access_token,
                    bi.txtProductTitleNewproduct.getText().toString().trim(),
                    bi.txtDetailsNewproduct.getText().toString().trim(),
                    categoryID,
                    subCategoryID,
                    bi.txtPriceNewproduct.getText().toString().trim(),
                    bi.txtQuantityNewproduct.getText().toString().trim(),
                    listUpdatedImages,
                    bi.txtStartingBidPriceNewproduct.getText().toString().trim(),
                    bi.txtAuctionExpiryDateNewproduct.getText().toString().trim(),
                    guestListStr,
                    imagesToDeleteID,
                    productResponse.productID,
                    bi.txtColorNewproduct.getText().toString().trim(),
                    bi.txtSizeNewproduct.getText().toString().trim());
        }

    }

    @Override
    public void onClickCategoryDropDown() {
        bi.spinnerCategoryNewproduct.showDropDown();
    }

    @Override
    public void onClickSubCategoryDropDown() {
        bi.spinnerSubCategoryNewproduct.showDropDown();
    }

    @Override
    public void updateSubCategories() {
        bi.spinnerSubCategoryNewproduct.setText("");
        bi.spinnerSubCategoryNewproduct.setHint("Categories");
        subCategoryID = "";
        bi.spinnerSubCategoryNewproduct.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, subCategoryList));
        bi.spinnerSubCategoryNewproduct.setEnabled(true);
        bi.btnSubCategoryDropDown.setClickable(true);
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
                .start();
    }

    @Override
    public void onSuccessfullyAddedProduct() {
//        bi.fabSaveAddnewproduct.setClickable(false);
        bi.multiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (!isFromEdit)
            SnackUtils.showSnackMessage(this, "Product Added Successfully");
        else
            SnackUtils.showSnackMessage(this, "Product Updated Successfully");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AddProductActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, 800);
    }

    @Override
    public void onErrorUploadProduct(String errorMsg) {
        bi.multiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        SnackUtils.showErrorMessage(this, errorMsg);
    }

    @Override
    public void showProductTitleError() {
        bi.txtProductTitleNewproduct.setError("Input Product Title");
    }

    @Override
    public void showProductDescError() {
        bi.txtDetailsNewproduct.setError("Input Product Description");
    }

    @Override
    public void showAmountError() {
        bi.txtPriceNewproduct.setError("Input Product Price");
    }

    @Override
    public void showQuantityError() {
        bi.txtQuantityNewproduct.setError("Input Quantity");
    }

    @Override
    public void showImageError() {
        SnackUtils.showSnackMessage(this, "Add Images To Add Product");
    }

    @Override
    public void showStartingBidError() {
        bi.txtStartingBidPriceNewproduct.setError("Input Starting Bid");
    }

    @Override
    public void showExpiryDateError() {
        bi.txtAuctionExpiryDateNewproduct.setError("Select Expiry Date");
    }

    @Override
    public void onClickExpiryDate() {
        DatePickerFragment dateDialog = new DatePickerFragment();
        dateDialog.setParams(this, bi.txtAuctionExpiryDateNewproduct, true);
        dateDialog.setRetainInstance(true);
        dateDialog.show(this.getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void updateProductDetails() {
        bi.txtProductTitleNewproduct.setText(productResponse.productTitle);
        bi.txtDetailsNewproduct.setText(productResponse.productDesc);
        bi.txtColorNewproduct.setText(productResponse.color == null?"":productResponse.color);
        bi.txtSizeNewproduct.setText(productResponse.size == null?"":productResponse.size);
        categoryID = productResponse.categoryID;
        subCategoryID = productResponse.subcategoryID;
        for (int i = 0; i < categoriesList.size(); i++) {
            if (categoriesList.get(i).categoryID.equals(categoryID)) {
                bi.spinnerCategoryNewproduct.setHint(categoriesList.get(i).categoryName);
                if (subCategoryID != null && !subCategoryID.equals("0") && !subCategoryID.isEmpty()) {
                    for (int j = 0; j < categoriesList.get(i).subcategories.size(); j++) {
                        if (categoriesList.get(i).subcategories.get(j).categoryID.equals(subCategoryID)) {
                            bi.spinnerSubCategoryNewproduct.setHint(categoriesList.get(i).subcategories.get(j).categoryName);
                        }
                    }
                }
            }
        }
        bi.txtPriceNewproduct.setText("" + String.valueOf(Double.valueOf(productResponse.price)));
        bi.txtQuantityNewproduct.setText(productResponse.quantity);
        for (ImgResponse imgItem :
                productResponse.imgNames) {
            mSelectedImages.add(new Image(Long.parseLong(imgItem.imageID), "", imgItem.imgName));
        }
        galleryAdapter.notifyDataSetChanged();
        if (productResponse.auctionable.equals("1")) {
            bi.switchAuctionableNewproduct.setChecked(true);
            bi.txtStartingBidPriceNewproduct.setText("" + (Long.parseLong(productResponse.startingBid)));
            bi.txtAuctionExpiryDateNewproduct.setText(productResponse.auctionExpDate);
        }
    }

    @Override
    public void onClickShowCategory() {
        bi.spinnerCategoryNewproduct.showDropDown();
    }

    @Override
    public void onClickShowSubCategory() {
        bi.spinnerSubCategoryNewproduct.showDropDown();
    }

    @Override
    public void onSuccessfullyUpdateProduct() {
//        bi.fabSaveAddnewproduct.setClickable(false);
        bi.multiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (!isFromEdit)
            SnackUtils.showSnackMessage(this, "Product Added Successfully");
        else
            SnackUtils.showSnackMessage(this, "Product Updated Successfully");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =new Intent(AddProductActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, 800);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            bi.layoutAuctionableContentNewproduct.setVisibility(View.VISIBLE);
            isAuctionable = true;
        } else {
            bi.layoutAuctionableContentNewproduct.setVisibility(View.GONE);
            isAuctionable = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            for (Image image : images) {
                File compressedImageFile = null;
                try {
                    compressedImageFile = new Compressor(this).compressToFile(new File(image.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (compressedImageFile != null){
                    image.setPath(compressedImageFile.getAbsolutePath());
                }
                mSelectedImages.add(image);
            }
        }
        bi.recyclerMiniGalleryNewproduct.setVisibility(View.VISIBLE);
        galleryAdapter.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Unit invoke(final View view, Image image, final Integer position, Map<Integer, ? extends View> integerMap) {
        final HorizontalImageItemLayoutBinding binding = DataBindingUtil.bind(view);
        View hover = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_product_image_hover, null);
        binding.blurProductItem.setHoverView(hover);
        binding.blurProductItem.setBlurDuration(10);
        binding.blurProductItem.enableZoomBackground(true);
        binding.blurProductItem.dismissHover();
        String img = mSelectedImages.get(position).getPath();
        Glide.with(AddProductActivity.this).load(img).into(binding.imgSelectedGallery);

        final ImageView imgDelete = (ImageView) hover.findViewById(R.id.imgDelete_gallery);
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgDelete.getVisibility() == View.INVISIBLE)
                    imgDelete.setVisibility(View.VISIBLE);
                binding.blurProductItem.toggleHover();
                imgDelete.setVisibility(View.INVISIBLE);
                if (isFromEdit) {
                    imageDeleteStringBuilder.append(String.valueOf(mSelectedImages.get((int) position).getId()));
                    imageDeleteStringBuilder.append(",");
                }
                mSelectedImages.remove((int) position);
                galleryAdapter.getItemList().remove((int)position);
                //galleryAdapter.getItemList().addAll(mSelectedImages);
                galleryAdapter.notifyDataSetChanged();
            }
        });

        return null;
    }

    @Override
    public Unit invoke(Image image, Integer position) {
        View view = layoutManager.findViewByPosition(position);
        return null;
    }


    @Override
    public void onTokenAdded(Person token) {
        guestsList.add(token);
    }

    @Override
    public void onTokenRemoved(Person token) {
        guestsList.remove(token);
    }
}
