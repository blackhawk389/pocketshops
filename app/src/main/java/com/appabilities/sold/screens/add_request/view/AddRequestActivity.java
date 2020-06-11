package com.appabilities.sold.screens.add_request.view;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivityAddRequestBinding;
import com.appabilities.sold.databinding.HorizontalImageItemLayoutBinding;
import com.appabilities.sold.networking.response.CategoriesResponse;
import com.appabilities.sold.networking.response.ImgResponse;
import com.appabilities.sold.networking.response.RequestResponse;
import com.appabilities.sold.networking.response.SubCategoryResponse;
import com.appabilities.sold.screens.add_product.view.AddProductActivity;
import com.appabilities.sold.screens.add_request.AddRequestContract;
import com.appabilities.sold.screens.add_request.AddRequestPresenter;
import com.appabilities.sold.screens.my_products.view.MyProductsActivity;
import com.appabilities.sold.screens.request.view.RequestActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.bumptech.glide.Glide;
import com.kennyc.view.MultiStateView;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;

import org.androidannotations.annotations.App;
import org.parceler.Parcels;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;

public class AddRequestActivity extends BaseActivity implements AddRequestContract.View<List<CategoriesResponse>>,
        Function4<View, Image, Integer, Map<Integer, ? extends View>, Unit>, Function2<Image, Integer, Unit> {

    AddRequestPresenter presenter;
    ActivityAddRequestBinding bi;
    List<String> categoryList;
    List<String> subCategoryList;
    List<CategoriesResponse> categoriesList;
    String categoryID, subCategoryID;
    private ArrayList<Image> mSelectedImages;
    private RecyclerAdapterUtil<Image> galleryAdapter;
    private LinearLayoutManager layoutManager;
    private RequestResponse requestResponse;
    private boolean isFromEdit = false;
    StringBuilder imageDeleteStringBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_add_request);
        bi.setView(this);
        presenter = new AddRequestPresenter(this);
        presenter.initScreen();
        bi.setPresenter(presenter);
    }

    @Override
    public void setupViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ADD REQUEST");

        mSelectedImages = new ArrayList<>();
        galleryAdapter = new RecyclerAdapterUtil<>(this, mSelectedImages, R.layout.horizontal_image_item_layout);
        galleryAdapter.addOnDataBindListener(this);
        galleryAdapter.addOnClickListener(this);

        bi.recyclerMiniGalleryNewrequest.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        bi.recyclerMiniGalleryNewrequest.setLayoutManager(layoutManager);
        bi.recyclerMiniGalleryNewrequest.setAdapter(galleryAdapter);
        presenter.getCategories();
        bi.spinnerCategoryNewrequest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categoryID = categoriesList.get(position).categoryID;
                if (categoryID.equals("18")){
                    bi.txtColorNewrequest.setVisibility(View.GONE);
                    bi.txtSizeNewrequest.setVisibility(View.GONE);
                }else if (categoryID.equals("2")){
                    bi.txtColorNewrequest.setVisibility(View.VISIBLE);
                    bi.txtSizeNewrequest.setVisibility(View.VISIBLE);
                }else {
                    bi.txtColorNewrequest.setVisibility(View.VISIBLE);
                    bi.txtSizeNewrequest.setVisibility(View.GONE);
                }

                subCategoryList = new ArrayList<String>();
                if (categoriesList.get(position).subcategories != null) {
                    for (SubCategoryResponse item :
                            categoriesList.get(position).subcategories) {
                        subCategoryList.add(item.categoryName);
                    }
                    updateSubCategories();
                } else {
                    bi.spinnerSubCategoryNewrequest.setEnabled(false);
                    bi.spinnerSubCategoryNewrequest.setText("");
                    bi.spinnerSubCategoryNewrequest.setHint("None");
                    subCategoryID = "";
                    bi.btnSubCategoryDropDown.setClickable(false);
                }
            }
        });

        bi.spinnerSubCategoryNewrequest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        bi.spinnerCategoryNewrequest.setThreshold(0);
        bi.spinnerSubCategoryNewrequest.setThreshold(0);

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
        bi.spinnerCategoryNewrequest.setAdapter(
                new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, categoryList));


        if (getIntent().hasExtra(AppConstants.KEYS.REQUEST_ITEM.name())){
            imageDeleteStringBuilder = new StringBuilder();
            requestResponse = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.REQUEST_ITEM.name()));
            updateRequest(requestResponse);
            isFromEdit = true;
            getSupportActionBar().setTitle("UPDATE REQUEST");
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
    public void updateSubCategories() {
        bi.spinnerSubCategoryNewrequest.setText("");
        bi.spinnerSubCategoryNewrequest.setHint("Categories");
        subCategoryID = "";
        bi.spinnerSubCategoryNewrequest.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, subCategoryList));
        bi.spinnerSubCategoryNewrequest.setEnabled(true);
        bi.btnSubCategoryDropDown.setClickable(true);
    }

    @Override
    public void onClickShowCategory() {
        bi.spinnerCategoryNewrequest.showDropDown();
    }

    @Override
    public void onClickCategoryDropDown() {
        bi.spinnerCategoryNewrequest.showDropDown();
    }

    @Override
    public void onClickShowSubCategory() {
        bi.spinnerSubCategoryNewrequest.showDropDown();
    }

    @Override
    public void onClickSubCategoryDropDown() {
        bi.spinnerSubCategoryNewrequest.showDropDown();
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
    public void onClickAddRequest() {
        if (categoryID == null || categoryID.equals("")) {
            SnackUtils.showSnackMessage(this, "Select Category To Add Request");
            return;
        }
        for (CategoriesResponse item :
                categoriesList) {
            if (categoryID.equals(item.categoryID)) {
                if (item.subcategories != null && item.subcategories.size() > 0) {
                    if (subCategoryID == null || subCategoryID.equals("")) {
                        SnackUtils.showSnackMessage(this, "Select Subcategory To Add Request");
                        return;
                    }
                }
            }
        }

        if (isFromEdit){
            String imagesToDeleteID = "";
            if (imageDeleteStringBuilder != null && imageDeleteStringBuilder.length() > 1)
                imagesToDeleteID = imageDeleteStringBuilder.substring(0, imageDeleteStringBuilder.length() - 1);
            List<Image> listUpdatedImages = new ArrayList<>();
            for (int i = 0; i < mSelectedImages.size(); i++) {
                if (!mSelectedImages.get(i).getPath().equals(requestResponse.imgNames.get(i).imgName)){
                    listUpdatedImages.add(mSelectedImages.get(i));
                }
            }
            presenter.editRequest(loginResponse.access_token,
                    bi.txtRequestTitle.getText().toString().trim(),
                    bi.txtRequestDetail.getText().toString().trim(),
                    categoryID,
                    subCategoryID,
                    bi.txtPriceNewproduct.getText().toString().trim(),
                    bi.txtQuantityNewproduct.getText().toString().trim(),
                    listUpdatedImages,
                    requestResponse.productID,
                    imagesToDeleteID,
                    bi.txtColorNewrequest.getText().toString().trim(),
                    bi.txtSizeNewrequest.getText().toString().trim());
        }else {
            presenter.addRequest(loginResponse.access_token,
                    bi.txtRequestTitle.getText().toString().trim(),
                    bi.txtRequestDetail.getText().toString().trim(),
                    categoryID,
                    subCategoryID,
                    bi.txtPriceNewproduct.getText().toString().trim(),
                    bi.txtQuantityNewproduct.getText().toString().trim(),
                    mSelectedImages,
                    bi.txtColorNewrequest.getText().toString().trim(),
                    bi.txtSizeNewrequest.getText().toString().trim());
        }
    }

    @Override
    public void showRequestTitleError() {
        bi.txtRequestTitle.setError("Input Request Title");
    }

    @Override
    public void showRequestDescError() {
        bi.txtRequestDetail.setError("Input Request Detail");
    }

    @Override
    public void showQuantityError() {
        bi.txtQuantityNewproduct.setError("Input Quantity");
    }

    @Override
    public void showImageError() {
        SnackUtils.showSnackMessage(this, "Add Images To Add Request");
    }

    @Override
    public void onSuccessfullyAddedRequest() {
        bi.fabSaveAddnewproduct.setClickable(false);
        bi.multiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (!isFromEdit)
            SnackUtils.showSnackMessage(this, "Request Added Successfully");
        else
            SnackUtils.showSnackMessage(this, "Request Updated Successfully");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(AddRequestActivity.this, RequestActivity.class));
                finish();
            }
        }, 800);
    }

    @Override
    public void onErrorUploadRequest(String errorMsg) {
        bi.multiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        SnackUtils.showSnackMessage(this, errorMsg);
    }

    @Override
    public void updateRequest(RequestResponse request) {
        bi.txtRequestTitle.setText(request.productTitle);
        bi.txtRequestDetail.setText(request.productDesc);
        bi.txtColorNewrequest.setText(request.color);
        bi.txtSizeNewrequest.setText(request.size);

        for (int i = 0; i < categoriesList.size(); i++) {
            if (categoriesList.get(i).categoryName.toLowerCase().trim().equals(request.categoryName.toLowerCase().trim())) {
                categoryID = categoriesList.get(i).categoryID;
                bi.spinnerCategoryNewrequest.setHint(categoriesList.get(i).categoryName);
               // if (subCategoryID != null && !subCategoryID.equals("0") && !subCategoryID.isEmpty()) {
                    for (int j = 0; j < categoriesList.get(i).subcategories.size(); j++) {
                        if (categoriesList.get(i).subcategories.get(j).categoryName.toLowerCase().trim().equals(request.subcategoryName.toLowerCase().trim())) {
                            subCategoryID = categoriesList.get(i).subcategories.get(j).categoryID;
                            bi.spinnerSubCategoryNewrequest.setHint(categoriesList.get(i).subcategories.get(j).categoryName);

                            subCategoryList = new ArrayList<>();
                            for (SubCategoryResponse item : categoriesList.get(i).subcategories) {
                                subCategoryList.add(item.categoryName);
                            }


                            bi.spinnerSubCategoryNewrequest.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, subCategoryList));

                            if (categoryID.equals("18")){
                                bi.txtColorNewrequest.setVisibility(View.GONE);
                                bi.txtSizeNewrequest.setVisibility(View.GONE);
                            }else if (categoryID.equals("2")){
                                bi.txtColorNewrequest.setVisibility(View.VISIBLE);
                                bi.txtSizeNewrequest.setVisibility(View.VISIBLE);
                            }else {
                                bi.txtColorNewrequest.setVisibility(View.VISIBLE);
                                bi.txtSizeNewrequest.setVisibility(View.GONE);
                            }
                        }

                    }
               // }
            }
        }
        bi.txtPriceNewproduct.setText("" + String.valueOf(Double.valueOf(request.price)));
        bi.txtQuantityNewproduct.setText(request.quantity);
        for (ImgResponse imgItem :
                request.imgNames) {
            mSelectedImages.add(new Image(Long.parseLong(imgItem.imageID), "", imgItem.imgName));
        }
        //galleryAdapter.getItemList().addAll(mSelectedImages);
        galleryAdapter.notifyDataSetChanged();
    }

    @Override
    public void updatedRequestSuccessfully() {
        bi.fabSaveAddnewproduct.setClickable(false);
        bi.multiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (!isFromEdit)
            SnackUtils.showSnackMessage(this, "Request Added Successfully");
        else
            SnackUtils.showSnackMessage(this, "Request Updated Successfully");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(AddRequestActivity.this, RequestActivity.class));
                finish();
            }
        }, 800);
    }

    @Override
    public void errorRequestUpdate(String msg) {
        bi.multiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        SnackUtils.showSnackMessage(this, msg);
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
        Glide.with(AddRequestActivity.this).load(img).into(binding.imgSelectedGallery);
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
                //galleryAdapter.getItemList().remove((int)position);
                //galleryAdapter.getItemList().addAll(mSelectedImages);
                galleryAdapter.notifyDataSetChanged();
            }
        });
        return null;
    }

    @Override
    public Unit invoke(Image image, Integer integer) {
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            for (Image image :
                    images) {
                File compressedImageFile = null;
                try {
                  //  String compressImage = compressImage(image.getPath());
                  //  compressedImageFile = new File(getRealPathFromURI(compressImage));
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
        bi.recyclerMiniGalleryNewrequest.setVisibility(View.VISIBLE);
        galleryAdapter.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {               imgRatio = maxHeight / actualHeight;
            actualWidth = (int) (imgRatio * actualWidth);
            actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }       final float totalPixels = width * height;       final float totalReqPixelsCap = reqWidth * reqHeight * 2;       while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}
