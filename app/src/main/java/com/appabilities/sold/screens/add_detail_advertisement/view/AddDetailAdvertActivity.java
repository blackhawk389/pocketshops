package com.appabilities.sold.screens.add_detail_advertisement.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivityAddDetailAdvertBinding;
import com.appabilities.sold.networking.response.AdCategoryResponse;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.PaymentResponse;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.screens.add_detail_advertisement.AddDetailAdvertisementContract;
import com.appabilities.sold.screens.add_detail_advertisement.AddDetailAdvertisementPresenter;
import com.appabilities.sold.screens.advertisement.view.AdvertisementActivity;
import com.appabilities.sold.screens.payment_webview.OnlinePaymentActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.oppwa.mobile.connect.checkout.dialog.CheckoutActivity;
import com.oppwa.mobile.connect.exception.PaymentError;
import com.oppwa.mobile.connect.provider.Transaction;
import com.oppwa.mobile.connect.provider.TransactionType;
import com.oppwa.mobile.connect.service.IProviderBinder;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class AddDetailAdvertActivity extends BaseActivity implements AddDetailAdvertisementContract.View, ValueChangedListener {

    private static final int TAKE_GALLERY = 1001;
    AddDetailAdvertisementPresenter presenter;
    ActivityAddDetailAdvertBinding bi;
    AdCategoryResponse advertItem;
    ProductResponse productItem;
    File advertImg;
    boolean isAdvertImgAttached = false;
    String totalAmount;
    private IProviderBinder binder;
    private boolean isPublicAd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_add_detail_advert);
        bi.setView(this);
        presenter = new AddDetailAdvertisementPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        advertItem = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.ADVERT_ITEM.name()));

        bi.setAdvertItem(advertItem);
        getSupportActionBar().setTitle(advertItem.advertType);
        if (advertItem.advertRID.equals("1")) {
            bi.layoutFeature.setVisibility(View.VISIBLE);
            bi.edtTitle.setVisibility(View.VISIBLE);
            bi.edtDesc.setVisibility(View.VISIBLE);
            bi.edtUrl.setVisibility(View.VISIBLE);
            bi.layoutCategory.setVisibility(View.GONE);
        } else {
            productItem = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.PRODUCT_ITEM.name()));
            bi.setProductItem(productItem);
            bi.executePendingBindings();
            bi.layoutFeature.setVisibility(View.GONE);
            bi.layoutCategory.setVisibility(View.VISIBLE);
            bi.edtTitle.setVisibility(View.GONE);
            bi.edtDesc.setVisibility(View.GONE);
            bi.edtUrl.setVisibility(View.GONE);
        }

        bi.numberPicker.setValueChangedListener(this);
        bi.txtTotalAmount.setText("SAR " + advertItem.rate);
        totalAmount = advertItem.rate;
    }

    @Override
    public void onClickUploadImage() {
        boolean fileGrant = ContextCompat.checkSelfPermission(AddDetailAdvertActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (fileGrant) {
            // Gallery
            getImageFromGallery();
        } else {
            new TedPermission(AddDetailAdvertActivity.this)
                    .setPermissionListener(permissionListener)
                    .setDeniedMessage(getString(R.string.rationale_camera))
                    .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        }
    }

    @Override
    public void getImageFromGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        startActivityForResult(gallery, TAKE_GALLERY);
    }

    @Override
    public void onClickSubmitAdvertisement() {
        bi.btnSubmitApproval.startAnimation();
        if (advertItem.advertRID.equals("1")) {
            if (advertImg == null) {
                imageNotSelected();
                return;
            } else if (bi.edtTitle.getText().toString().trim().isEmpty()) {
                titleError("Input title to continue");
                return;
            }
            if (!bi.edtUrl.getText().toString().trim().isEmpty()){
                if (!Patterns.WEB_URL.matcher(bi.edtUrl.getText().toString().trim()).matches()) {
                    invalidURL("Enter valid url");
                    return;
                }
            }

            if (!totalAmount.contains(".")){
                totalAmount = totalAmount + ".00";
            }
            isPublicAd = true;
            String merchantUserID = loginResponse.userID + "" + new Date().toString();
            presenter.getCheckoutID(totalAmount, merchantUserID);

        }
        else {
            if (!totalAmount.contains(".")){
                totalAmount = totalAmount + ".00";
            }
            isPublicAd = false;
            String merchantUserID = loginResponse.userID + "" + new Date().toString();
            presenter.getCheckoutID(totalAmount, merchantUserID);
        }

    }

    @Override
    public String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void imageNotSelected() {
        bi.btnSubmitApproval.revertAnimation();
        SnackUtils.showSnackMessage(this, "Select Image For Feature Advertisement");
    }

    @Override
    public void onSuccessfullSubmitAdvert(BaseResponse body) {
        bi.btnSubmitApproval.revertAnimation();

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_view_advert, null);


        new AlertDialog.Builder(AddDetailAdvertActivity.this)
                .setView(dialogView)
                .show();

        Button btnFinish = (Button) dialogView.findViewById(R.id.btn_done);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddDetailAdvertActivity.this, AdvertisementActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

//        MaterialDialog dialog = new MaterialDialog.Builder(this)
//                .customView(R.layout.custom_view_advert, false)
//                .show();

//        View view = dialog.getCustomView();
//        Button btnFinish = (Button) view.findViewById(R.id.btn_done);
//        btnFinish.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AddDetailAdvertActivity.this, AdvertisementActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
//            }
//        });
    }

    @Override
    public void errorInSubmitAdvert() {
        bi.btnSubmitApproval.revertAnimation();
        SnackUtils.showSnackMessage(this, "Error in submitting advertisement", false);
    }

    @Override
    public void titleError(String s) {
        bi.edtTitle.setError(s);
        bi.edtTitle.requestFocus();
        bi.btnSubmitApproval.revertAnimation();
    }

    @Override
    public void invalidURL(String s) {
        bi.edtUrl.setError(s);
        bi.edtUrl.requestFocus();
        bi.btnSubmitApproval.revertAnimation();
    }

    @Override
    public void updateCheckoutID(PaymentResponse body) {
        bi.btnSubmitApproval.revertAnimation();
        /*Set<String> paymentBrands = new LinkedHashSet<String>();
        paymentBrands.add("VISA");
        paymentBrands.add("MASTER");
        paymentBrands.add("DIRECTDEBIT_SEPA");

        CheckoutSettings checkoutSettings = new CheckoutSettings(body.id, paymentBrands);
        checkoutSettings.setWebViewEnabledFor3DSecure(true);
        Intent intent = new Intent(AddDetailAdvertActivity.this, CheckoutActivity.class);
        intent.putExtra(CheckoutActivity.CHECKOUT_SETTINGS, checkoutSettings);
        startActivityForResult(intent, CheckoutActivity.CHECKOUT_ACTIVITY);*/


        Intent intent = new Intent(this, OnlinePaymentActivity.class);
        if(isPublicAd){
            intent.putExtra("checkoutId", body.id);
            intent.putExtra("advertRID", advertItem.advertRID);
            intent.putExtra("advert_no_days", String.valueOf(bi.numberPicker.getValue()));
            intent.putExtra("rate", advertItem.rate);
            intent.putExtra("productID", "");
            intent.putExtra("totalAmount", totalAmount);
            intent.putExtra("advertImg", advertImg);
            intent.putExtra("url", bi.edtUrl.getText().toString().trim());
            intent.putExtra("title", bi.edtTitle.getText().toString().trim());
            intent.putExtra("desc", bi.edtDesc.getText().toString().trim());

        }
        else {
            intent.putExtra("checkoutId", body.id);
            intent.putExtra("advertRID", advertItem.advertRID);
            intent.putExtra("advert_no_days", String.valueOf(bi.numberPicker.getValue()));
            intent.putExtra("rate", advertItem.rate);
            intent.putExtra("productID", productItem.productID);
            intent.putExtra("totalAmount", totalAmount);
            intent.putExtra("advertImg", advertImg);
            intent.putExtra("url", "");
            intent.putExtra("title", "");
            intent.putExtra("desc", "");
        }

        startActivity(intent);
    }

    @Override
    public void errorInGettingCheckoutID() {
        bi.btnSubmitApproval.revertAnimation();
        SnackUtils.showSnackMessage(this, "Error In Getting CheckoutID");
    }

    @Override
    public void onError(String error) {
        bi.btnSubmitApproval.revertAnimation();
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    // Permissions
    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            boolean fileGrant = ContextCompat.checkSelfPermission(AddDetailAdvertActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            if (fileGrant) {
                Log.d("SOLD", "Storage Granted");
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> arrayList) {

        }
    };

    @Override
    public void valueChanged(int value, ActionEnum action) {
        bi.txtTotalAmount.setText("SAR " + (value * Integer.parseInt(advertItem.rate)));
        totalAmount = "" + (value * Integer.parseInt(advertItem.rate));
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*Intent intent = new Intent(this, ConnectService.class);

        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);*/
    }

    @Override
    protected void onStop() {
        super.onStop();

        /*unbindService(serviceConnection);
        stopService(new Intent(this, ConnectService.class));*/
    }

   /* private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (IProviderBinder) service;
        *//* we have a connection to the service *//*
            try {
                binder.initializeProvider(Connect.ProviderMode.LIVE);
            } catch (PaymentException ee) {
        *//* error occurred *//*
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == TAKE_GALLERY || resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            advertImg = new File(getRealPathFromURI(selectedImage));
            isAdvertImgAttached = true;

            bi.imgUpload.setImageURI(selectedImage);
        }else if (resultCode == CheckoutActivity.RESULT_OK){
             /* transaction completed */
            Transaction transaction = data.getParcelableExtra(CheckoutActivity.CHECKOUT_RESULT_TRANSACTION);
            bi.btnSubmitApproval.startAnimation();
            if (advertItem.advertRID.equals("2")){

            } else if (advertItem.advertRID.equals("1")){

            }

            /* resource path if needed */
            String resourcePath = data.getStringExtra(CheckoutActivity.CHECKOUT_RESULT_RESOURCE_PATH);

            if (transaction.getTransactionType() == TransactionType.SYNC) {
                /* check the paymentErrorResult of synchronous transaction */
            } else {
                /* wait for the asynchronous transaction callback in the onNewIntent() */
            }
        }else if (resultCode == CheckoutActivity.RESULT_CANCELED){
            /* shopper canceled the checkout process */
        }else if (resultCode == CheckoutActivity.RESULT_ERROR){
            /* error occurred */
            PaymentError error = data.getParcelableExtra(CheckoutActivity.CHECKOUT_RESULT_ERROR);
        }
    }
}
