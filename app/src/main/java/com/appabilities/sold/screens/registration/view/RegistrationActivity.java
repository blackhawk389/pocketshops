package com.appabilities.sold.screens.registration.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivityRegistrationBinding;
import com.appabilities.sold.networking.response.VerifyUserResponse;
import com.appabilities.sold.screens.create_profile.view.CreateProfileActivity;
import com.appabilities.sold.screens.registration.RegistrationContract;
import com.appabilities.sold.screens.registration.RegistrationPresenter;
import com.appabilities.sold.utils.MyBitmapUtils;
import com.appabilities.sold.utils.SnackUtils;

public class RegistrationActivity extends BaseActivity implements RegistrationContract.View<VerifyUserResponse> {

    ActivityRegistrationBinding bi;
    RegistrationPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        bi.setView(this);
        presenter = new RegistrationPresenter(this);
        presenter.initScreen();
        bi.setPresenter(presenter);
    }

    @Override
    public void setupViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ca.getResourceColor(R.color.colorPrimaryDark));
        }
    }

    @Override
    public void onLoading() {
        bi.btnNextRegister.startAnimation();
    }

    @Override
    public void onRequestSuccessful(VerifyUserResponse responseData) {

        Bitmap icon = MyBitmapUtils.getBitmapFromVectorDrawable(this, R.drawable.ic_check_white);
        bi.btnNextRegister.doneLoadingAnimation(getResources().getColor(R.color.colorPrimary), icon);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                Intent ii = new Intent(RegistrationActivity.this, CreateProfileActivity.class);
                ii.putExtra("username", bi.txtUsernameRegister.getText().toString());
                ii.putExtra("email", bi.txtEmailRegister.getText().toString());
                ii.putExtra("password", bi.txtPasswordRegister.getText().toString());
                finish();
                startActivity(ii);
            }
        }, 800);
    }

    @Override
    public void onRequestFail(String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty())
            SnackUtils.showSnackMessage(this, errorMessage);
        Bitmap icon = MyBitmapUtils.getBitmapFromVectorDrawable(this, R.drawable.ic_clear_black_24dp);
        bi.btnNextRegister.doneLoadingAnimation(getResources().getColor(android.R.color.holo_red_dark), icon);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                bi.btnNextRegister.revertAnimation();
            }
        }, 800);
    }

    @Override
    public void onClickLoginHere() {
        finish();
    }

    @Override
    public void onNextClick() {
        ca.hideKeyboard(RegistrationActivity.this);
        presenter.verifyUser(bi.txtUsernameRegister.getText().toString().trim(),
                bi.txtEmailRegister.getText().toString().trim(),
                bi.txtPasswordRegister.getText().toString().trim(),
                bi.txtConfirmPasswordRegister.getText().toString().trim());
    }

    @Override
    public void showUsernameError(String s) {
        bi.txtUsernameRegister.setError(s);
        bi.txtUsernameRegister.requestFocus();
    }

    @Override
    public void showEmailError(String s) {
        bi.txtEmailRegister.setError(s);
        bi.txtEmailRegister.requestFocus();
    }

    @Override
    public void showPasswordError(String s) {
        bi.txtPasswordRegister.setError(s);
        bi.txtPasswordRegister.requestFocus();
    }

    @Override
    public void showConfirmPassError(String s) {
        bi.txtConfirmPasswordRegister.setError(s);
        bi.txtConfirmPasswordRegister.requestFocus();
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

}
