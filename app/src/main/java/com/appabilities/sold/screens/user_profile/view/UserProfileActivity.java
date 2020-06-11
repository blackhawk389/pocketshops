package com.appabilities.sold.screens.user_profile.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.appabilities.sold.BuildConfig;
import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.base.sidemenu.SideMenuBaseActivity;
import com.appabilities.sold.databinding.ActivityUserProfileBinding;
import com.appabilities.sold.model.GetFollowers;
import com.appabilities.sold.model.UserModel;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.screens.create_profile.view.CreateProfileActivity;
import com.appabilities.sold.screens.user_profile.UserProfileContract;
import com.appabilities.sold.screens.user_profile.UserProfilePresenter;
import com.appabilities.sold.screens.user_profile.fragment.my_product.view.MyProductListFragment;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.raizlabs.android.dbflow.sql.language.SQLite;


import java.io.File;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class UserProfileActivity extends BaseActivity implements UserProfileContract.View {

    ActivityUserProfileBinding bi;
    UserProfilePresenter presenter;
    ViewPagerAdapter pagerAdapter;
    private static final int TAKE_PICTURE = 120;
    private static final int TAKE_GALLERY = 1;
    File coverFile;
    private String mCurrentPhotoPath;
    boolean isCoverAttached = false;
    private File photoFile;
    // public LoginResponse loginResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_user_profile);
        bi.setView(this);
        presenter = new UserProfilePresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        //initDrawer(bi.toolbar, "MY ACCOUNT");
        changeStatusBarColor();
        setSupportActionBar(bi.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //loginResponse = SQLite.select().from(LoginResponse.class).querySingle();
        setUpViewpager();


        bi.txtUsernameViewmyprofile.setText(loginResponse.display_name.isEmpty()?loginResponse.username:loginResponse.display_name);
//        Glide.with(this)
//                .load(loginResponse.cover_image)
//                .into(bi.imgCover);
//        Glide.with(this)
//                .load(loginResponse.avatar)
//                .into(bi.imgProfileViewmyprofile);
        bi.imgCover.setImageURI(loginResponse.cover_image);
    }




    @Override
    public void setUpViewpager() {
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new MyProductListFragment(), "Products");
        bi.pb.setVisibility(View.VISIBLE);
        presenter.getFollowers(loginResponse.access_token);

    }

    @Override
    public void onClickEditCover() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(UserProfileActivity.this);
        builderSingle.setTitle("Load Picture From");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(UserProfileActivity.this, android.R.layout.simple_list_item_1);
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
                if (which == 0)
                {

                    boolean cameraGrant = ContextCompat.checkSelfPermission(UserProfileActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
                    boolean fileGrant = ContextCompat.checkSelfPermission(UserProfileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                    if (cameraGrant && fileGrant)
                    {
                        getImageFromCamera();
                    }
                    else {
                        new TedPermission(UserProfileActivity.this)
                                .setPermissionListener(permissionListener)
                                .setDeniedMessage(getString(R.string.rationale_camera))
                                .setPermissions(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .check();
                    }
                }
                else
                {
                    boolean fileGrant = ContextCompat.checkSelfPermission(UserProfileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                    if (fileGrant)
                    {
                        // Gallery
                        getImageFromGallery();
                    }
                    else {
                        new TedPermission(UserProfileActivity.this)
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
//        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
//                .title("Load Picture From")
      //         .items(R.array.pic_load_array)
//                .itemsCallback(new MaterialDialog.ListCallback() {
//                    @Override
//                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//
//                        if (which == 0)
//                        {
//
//                            boolean cameraGrant = ContextCompat.checkSelfPermission(UserProfileActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
//                            boolean fileGrant = ContextCompat.checkSelfPermission(UserProfileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//
//                            if (cameraGrant && fileGrant)
//                            {
//                                getImageFromCamera();
//                            }
//                            else {
//                                new TedPermission(UserProfileActivity.this)
//                                        .setPermissionListener(permissionListener)
//                                        .setDeniedMessage(getString(R.string.rationale_camera))
//                                        .setPermissions(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                        .check();
//                            }
//                        }
//                        else
//                        {
//                            boolean fileGrant = ContextCompat.checkSelfPermission(UserProfileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//
//                            if (fileGrant)
//                            {
//                                // Gallery
//                                getImageFromGallery();
//                            }
//                            else {
//                                new TedPermission(UserProfileActivity.this)
//                                        .setPermissionListener(permissionListener)
//                                        .setDeniedMessage(getString(R.string.rationale_camera))
//                                        .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                        .check();
//                            }
//
//
//                        }
//                    }
//                }).show();
    }

    @Override
    public void successfullyUpdateCover(LoginResponse loginData) {
        loginResponse.setCover_image(loginData.cover_image);
        loginResponse.save();
        //loginResponse.save();
        //loginResponse = SQLite.select().from(LoginResponse.class).querySingle();
//           Glide.with(getApplicationContext())
//                .load(loginResponse.cover_image)
//                .into(bi.imgCover);
        bi.imgCover.setImageURI(loginResponse.cover_image);
        bi.pb.setVisibility(View.GONE);
        SnackUtils.showSnackMessage(this, "Cover Updated Successfully");
    }

    @Override
    public void errorUpdateCover(String msg) {
        bi.pb.setVisibility(View.GONE);
        SnackUtils.showSnackMessage(this, msg);
    }

    @Override
    public void onGetFollowers(GetFollowers data) {
        bi.pb.setVisibility(View.GONE);
        pagerAdapter.addFragment(new UsersListFragment((ArrayList<UserModel>) data.getFollowersDetail(), true),  "Followers");
        pagerAdapter.addFragment(new UsersListFragment((ArrayList<UserModel>)data.getFollowingDetail(), false),  "Following");
        bi.viewpager.setAdapter(pagerAdapter);
        bi.tabsProfileViewmyprofile.setupWithViewPager(bi.viewpager);

    }

    @Override
    public void oGetFollowersError() {
        bi.pb.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuBtn_edit:
                Intent ii = new Intent(this, CreateProfileActivity.class);
                ii.putExtra(AppConstants.KEYS.EDIT_MODE.name(), true);
                startActivity(ii);
                return true;

            case R.id.menuBtn_share:
                SnackUtils.showSnackMessage(this, "Profile Shared!");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
                        FileProvider.getUriForFile(UserProfileActivity.this,"com.appabilities.sold.utils.GenericFileProvider", photoFile));
                //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, TAKE_PICTURE);
            }
        }
    }

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

        coverFile = image;

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "content:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Camera Image
        if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK)
        {
            onCaptureImageResult(data);
        }
        // Gallery Image
        else if(requestCode == TAKE_GALLERY && resultCode==Activity.RESULT_OK)
        {
            Uri selectedImage = data.getData();
            coverFile = new File(getRealPathFromURI(selectedImage));
            isCoverAttached = true;
            bi.imgCover.setImageURI(selectedImage);
            updateProfilePic(coverFile);
        }
    }

    private void onCaptureImageResult(Intent data) {
//        Bitmap thumbnail = (Bitmap) photoFile;
        //    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//
//        File destination = new File(Environment.getExternalStorageDirectory(),
//                System.currentTimeMillis() + ".jpg");
//        FileOutputStream fo;
//        try {
//           // destination.createNewFile();
//            fo = new FileOutputStream(photoFile);
//            fo.write(bytes.toByteArray());
//            fo.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        coverFile =  photoFile ;
        isCoverAttached = true;
       // Uri selectedImage = data.getData();

        Glide.with(this)
                .load(mCurrentPhotoPath)
                .into(bi.imgCover);
       // bi.imgCover.setImageURI(mCurrentPhotoPath);
        updateProfilePic(coverFile);
    }

    private void updateProfilePic(File file) {
        bi.pb.setVisibility(View.VISIBLE);

        presenter.postCover(loginResponse.access_token, file);
    }

    // Permissions
    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            boolean cameraGrant = ContextCompat.checkSelfPermission(UserProfileActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
            boolean fileGrant = ContextCompat.checkSelfPermission(UserProfileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
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

    @Override
    public void onResume() {
        super.onResume();
        loginResponse = SQLite.select().from(LoginResponse.class).querySingle();
        bi.imgProfileViewmyprofile.setImageURI(loginResponse.avatar);
        bi.txtUsernameViewmyprofile.setText(loginResponse.display_name.isEmpty()?loginResponse.username:loginResponse.display_name);
        //selectMenuOption(R.id.nav_my_account);
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



    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
