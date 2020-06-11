package com.appabilities.sold.screens.create_profile.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivityCreateProfileBinding;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.networking.response.RegisterUserResponse;
import com.appabilities.sold.screens.create_profile.CreateProfileContract;
import com.appabilities.sold.screens.create_profile.CreateProfilePresenter;
import com.appabilities.sold.screens.select_categories.view.SelectCategoriesActivity;
import com.appabilities.sold.screens.user_profile.view.UserProfileActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.MyBitmapUtils;
import com.appabilities.sold.utils.SnackUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CreateProfileActivity extends BaseActivity implements CreateProfileContract.View<RegisterUserResponse> {

    ActivityCreateProfileBinding bi;
    CreateProfilePresenter presenter;
    String username, email, password;
    boolean editMode;
    private static final int TAKE_PICTURE = 120;
    private static final int TAKE_GALLERY = 1;
    private String mCurrentPhotoPath;

    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_create_profile);
        bi.setView(this);
        presenter = new CreateProfilePresenter(this);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ca.getResourceColor(R.color.colorPrimaryDark));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.countries_array));
        bi.autoCompleteSpinner.setAdapter(adapter);

        if (getIntent().getExtras() != null)
        {
            Bundle bundle = getIntent().getExtras();
            editMode = bundle.getBoolean(AppConstants.KEYS.EDIT_MODE.name(), false);

            if(!editMode) {
                username = bundle.getString("username");
                email = bundle.getString("email");
                password = bundle.getString("password");
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Update Profile");
                setUserInfo();
                bi.btnNextCreateProfile.setText("Update");
                bi.txtSkipCreateProfile.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }




    @Override
    public void onLoading() {
        bi.btnNextCreateProfile.startAnimation();
        bi.txtSkipCreateProfile.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRequestSuccessful(RegisterUserResponse data) {
        Bitmap icon = MyBitmapUtils.getBitmapFromVectorDrawable(this, R.drawable.ic_check_white);
        bi.btnNextCreateProfile.doneLoadingAnimation(getResources().getColor(R.color.colorPrimary), icon);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.localID = 1;
        loginResponse.userID = data.userID;
        loginResponse.username = username;
        loginResponse.email = email;
        loginResponse.display_name = bi.txtDisplayNameCreateProfile.getText().toString().trim();
        loginResponse.avatar = data.avatar;
        loginResponse.phone = bi.txtPhoneCreateProfile.getText().toString().trim();
        loginResponse.country = bi.autoCompleteSpinner.getText().toString().trim();
        loginResponse.region = bi.txtRegionCreateProfile.getText().toString().trim();
        loginResponse.address = bi.txtAddressCreateProfile.getText().toString().trim();
        loginResponse.access_token = data.access_token;
        loginResponse.description = bi.txtDescCreateProfile.getText().toString().trim();
        loginResponse.save();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent ii = new Intent(CreateProfileActivity.this, SelectCategoriesActivity.class);
                startActivity(ii);
                CreateProfileActivity.this.finish();
            }
        }, 800);
    }

    @Override
    public void onRequestFail(String errorMessage) {
        if (editMode){
            bi.btnNextCreateProfile.setText("");
            bi.btnNextCreateProfile.setText("Update");
            bi.txtSkipCreateProfile.setVisibility(View.GONE);
        }

        if (errorMessage != null && !errorMessage.isEmpty())
            SnackUtils.showSnackMessage(this, errorMessage);
        Bitmap icon = MyBitmapUtils.getBitmapFromVectorDrawable(this, R.drawable.ic_clear_black_24dp);
        bi.btnNextCreateProfile.doneLoadingAnimation(getResources().getColor(android.R.color.holo_red_dark), icon);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                bi.btnNextCreateProfile.revertAnimation();
            }
        }, 800);
    }

    @Override
    public void onNextCreateProfileClick() {
        if (!editMode){
            registerUser();
        }else {
            presenter.updateUser(loginResponse.access_token,
                    bi.txtDisplayNameCreateProfile.getText().toString().trim().toString(),
                    bi.txtAddressCreateProfile.getText().toString().trim(),
                    bi.txtPhoneCreateProfile.getText().toString().trim(),
                    bi.autoCompleteSpinner.getText().toString().trim(),
                    bi.txtRegionCreateProfile.getText().toString(),
                    bi.txtDescCreateProfile.getText().toString().trim(),
                    avatarfile);
        }
    }

    @Override
    public void onSkipClick() {
        registerUser();
    }


    private void registerUser(){
        presenter.registerUser(username,
                email,
                password,
                bi.txtDisplayNameCreateProfile.getText().toString().trim(),
                bi.txtAddressCreateProfile.getText().toString().trim(),
                bi.txtPhoneCreateProfile.getText().toString().trim(),
                bi.autoCompleteSpinner.getText().toString().trim(),
                bi.txtRegionCreateProfile.getText().toString(),
                bi.txtDescCreateProfile.getText().toString().trim(),
                avatarfile);
    }

    @Override
    public void onProfileClick() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(CreateProfileActivity.this);
        builderSingle.setTitle("Load Picture From");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CreateProfileActivity.this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Camera");
        arrayAdapter.add("Gallery");


        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                String strName = arrayAdapter.getItem(which);
//                AlertDialog.Builder builderInner = new AlertDialog.Builder(CreateProfileActivity.this);
//                builderInner.setMessage(strName);
//                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog,int which) {
                        if (which == 0)
                        {

                            boolean cameraGrant = ContextCompat.checkSelfPermission(CreateProfileActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
                            boolean fileGrant = ContextCompat.checkSelfPermission(CreateProfileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                            if (cameraGrant && fileGrant)
                            {
                                getImageFromCamera();
                            }
                            else {
                                new TedPermission(CreateProfileActivity.this)
                                        .setPermissionListener(permissionListener)
                                        .setDeniedMessage(getString(R.string.rationale_camera))
                                        .setPermissions(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        .check();
                            }
                        }
                        else
                        {
                            boolean fileGrant = ContextCompat.checkSelfPermission(CreateProfileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                            if (fileGrant)
                            {
                                // Gallery
                                getImageFromGallery();
                            }
                            else {
                                new TedPermission(CreateProfileActivity.this)
                                        .setPermissionListener(permissionListener)
                                        .setDeniedMessage(getString(R.string.rationale_camera))
                                        .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        .check();
                            }


                        }
                 //   }
               // });
              //  builderInner.show();
            }
        });
        builderSingle.show();
//        final MaterialDialog materialDialog = new MaterialDialog.Builder(this)
//                .title("Load Picture From")
               // .items(R.array.pic_load_array)
//                .itemsCallback(new MaterialDialog.ListCallback() {
//                    @Override
//                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//
//                        if (which == 0)
//                        {
//
//                            boolean cameraGrant = ContextCompat.checkSelfPermission(CreateProfileActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
//                            boolean fileGrant = ContextCompat.checkSelfPermission(CreateProfileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//
//                            if (cameraGrant && fileGrant)
//                            {
//                                getImageFromCamera();
//                            }
//                            else {
//                                new TedPermission(CreateProfileActivity.this)
//                                        .setPermissionListener(permissionListener)
//                                        .setDeniedMessage(getString(R.string.rationale_camera))
//                                        .setPermissions(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                        .check();
//                            }
//                        }
//                        else
//                        {
//                            boolean fileGrant = ContextCompat.checkSelfPermission(CreateProfileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//
//                            if (fileGrant)
//                            {
//                                // Gallery
//                                getImageFromGallery();
//                            }
//                            else {
//                                new TedPermission(CreateProfileActivity.this)
//                                        .setPermissionListener(permissionListener)
//                                        .setDeniedMessage(getString(R.string.rationale_camera))
//                                        .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                        .check();
//                            }
//
//
//                        }
//                    }
//                })
//                .show();
    }

    public void getImageFromGallery()
    {
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        startActivityForResult(gallery, TAKE_GALLERY);
    }



    public void getImageFromCamera()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i("SOLD", "IOException");

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        FileProvider.getUriForFile(CreateProfileActivity.this,"com.appabilities.sold.utils.GenericFileProvider", photoFile));
                //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, TAKE_PICTURE);
            }
        }
    }

    // Permissions
    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            boolean cameraGrant = ContextCompat.checkSelfPermission(CreateProfileActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
            boolean fileGrant = ContextCompat.checkSelfPermission(CreateProfileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            if (cameraGrant) {
                Log.d("SOLD", "Camera Granted");
            }
            if (fileGrant) {
                Log.d("SOLD", "Storage Granted");
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> arrayList) {

        }
    };



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        avatarfile = image;

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "content:" + image.getAbsolutePath();
       // mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;


//        File imageFile = new File(filePath);
//        OutputStream  fout = new FileOutputStream(file);
//        Bitmap bitmap= BitmapFactory.decodeFile(filePath);
//        bitmap.compress(CompressFormat.JPEG, 80, fout);
//        fout.flush();
//        fout.close();

        // return imageFile;
    }

    //String encodedImage;
    File avatarfile;
    boolean isAvatarAttached = false;

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Camera Image
        if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK)
        {
            try {
               // try {
                String compressImage = compressImage(mCurrentPhotoPath);
                avatarfile = new File(getRealPathFromURI(compressImage));

                    Glide.with(this)
                            .load(compressImage)
                            .apply(RequestOptions.circleCropTransform())
                            .into(bi.imgProfile);


//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//                Matrix matrix = new Matrix();
//                matrix.postRotate(ca.getImageOrientation(avatarfile.getAbsolutePath()));
//                Bitmap rotatedBitmap = Bitmap.createBitmap(mImageBitmap, 0, 0, mImageBitmap.getWidth(), mImageBitmap.getHeight(), matrix, true);
//                bi.imgProfile.setImageBitmap(rotatedBitmap);
//
//                avatarfile = new File(mCurrentPhotoPath);
//
//
//                // Creating Base64 String
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
//                byte[] b = baos.toByteArray();
//
 //               encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
               // bi.imgProfile.setImageBitmap(mImageBitmap);

          //      bi.imgProfile.setImageURI(mCurrentPhotoPath);
                isAvatarAttached = true;

            } catch (OutOfMemoryError e) {
                BitmapFactory.Options options = new BitmapFactory.Options();

            }

        }
        // Gallery Image
        else if(requestCode == TAKE_GALLERY && resultCode==Activity.RESULT_OK)
        {
            if(data != null){
                Uri selectedImage = data.getData();
                avatarfile = new File(getRealPathFromURI(selectedImage));
                isAvatarAttached = true;
                bi.imgProfile.setImageURI(selectedImage);
            }

            /*
            // Creating Base64 String
            // Let's read picked image path using content resolver
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            cursor.close();


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();

            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            */

            //bi.imgProfile.setImageURI(photoFile);






        }
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

    @Override
    public void onDropDownClick() {
        bi.autoCompleteSpinner.showDropDown();
    }

    @Override
    public void setUserInfo() {
        if (loginResponse.avatar != null && !loginResponse.avatar.equals(""))
            bi.imgProfile.setImageURI(loginResponse.avatar);

        bi.txtDisplayNameCreateProfile.setText(loginResponse.display_name);
        bi.txtPhoneCreateProfile.setText(loginResponse.phone);
        bi.txtDescCreateProfile.setText(loginResponse.description);

        bi.autoCompleteSpinner.setText(loginResponse.country);
        bi.txtRegionCreateProfile.setText(loginResponse.region);
        bi.txtAddressCreateProfile.setText(loginResponse.address);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onUpdateSuccessful(LoginResponse body) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setLocalID(1);
        loginResponse.setUserID(body.userID);
        loginResponse.setUsername(body.username);
        loginResponse.setEmail(body.email);
        loginResponse.setDisplay_name(body.display_name);
        loginResponse.setPhone(body.phone);
        loginResponse.setCountry(body.country);
        loginResponse.setRegion(body.region);
        loginResponse.setAddress(body.address);
        loginResponse.setAccess_token(body.access_token);
        loginResponse.setDescription(body.description);
        loginResponse.setAvatar(body.avatar);
        loginResponse.save();
        bi.btnNextCreateProfile.revertAnimation();
//        Glide.with(this)
//                .load(loginResponse.avatar)
//                .into( bi.imgProfile);
        bi.imgProfile.setImageURI(loginResponse.avatar);
        SnackUtils.showSnackMessage(this, "Profile Updated Successfully");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CreateProfileActivity.this.finish();
            }
        }, 500);
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
            if (imgRatio < maxRatio) {               imgRatio = maxHeight / actualHeight;                actualWidth = (int) (imgRatio * actualWidth);               actualHeight = (int) maxHeight;             } else if (imgRatio > maxRatio) {
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


    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

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

    //
    private void previewMedia(boolean isImage) {


        // Checking whether captured media is image or video
        if (isImage) {

            // vidPreview.setVisibility(View.GONE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();


            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 2;


            final Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
            options.inJustDecodeBounds = true;


        }
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

}


