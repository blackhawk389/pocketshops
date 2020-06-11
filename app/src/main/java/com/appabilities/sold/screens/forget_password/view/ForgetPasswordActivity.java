package com.appabilities.sold.screens.forget_password.view;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.databinding.DataBindingUtil;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivityForgetPasswordBinding;
import com.appabilities.sold.screens.forget_password.ForgetPasswordContract;
import com.appabilities.sold.screens.forget_password.ForgetPasswordPresenter;
import com.appabilities.sold.utils.MyBitmapUtils;
import com.appabilities.sold.utils.SnackUtils;
import com.appabilities.sold.utils.TextValidatiors;

public class ForgetPasswordActivity extends BaseActivity implements
        ForgetPasswordContract.View {

    ActivityForgetPasswordBinding bi;
    ForgetPasswordPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_forget_password);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_forget_password);
        presenter = new ForgetPasswordPresenter(this);
        bi.setPresenter(presenter);
        bi.setView(this);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {

        // Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onResetButtonClick() {
        presenter.resetPassword(bi.txtEmailAddress.getText().toString());
    }

    @Override
    public void showProgress() {
        bi.btnReset.startAnimation();
    }

    @Override
    public void showSuccess(String msg) {
        SnackUtils.showSnackMessage(ForgetPasswordActivity.this, msg, true);
        Bitmap icon = MyBitmapUtils.getBitmapFromVectorDrawable(this, R.drawable.ic_check_white);
        bi.btnReset.doneLoadingAnimation(ca.getResourceColor(R.color.colorPrimary), icon);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                finish();
            }
        }, 700);
    }

    @Override
    public void showFailure() {
        Bitmap icon = MyBitmapUtils.getBitmapFromVectorDrawable(this, R.drawable.ic_clear_black_24dp);
        bi.btnReset.doneLoadingAnimation(getResources().getColor(android.R.color.holo_red_dark), icon);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                bi.btnReset.revertAnimation();
            }
        }, 800);
    }

    @Override
    public void showFailure(String message) {
        showFailure();
        SnackUtils.showSnackMessage(ForgetPasswordActivity.this, message, false);
    }

    @Override
    public void showEmailError(String s) {
        bi.txtEmailAddress.setError(s);
        bi.txtEmailAddress.requestFocus();
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
