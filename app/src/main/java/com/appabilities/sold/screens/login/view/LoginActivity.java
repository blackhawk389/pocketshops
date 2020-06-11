package com.appabilities.sold.screens.login.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.appabilities.sold.R;
import com.appabilities.sold.SplashActivity;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.database.tables.RelationshipModel;
import com.appabilities.sold.databinding.ActivityLoginBinding;
import com.appabilities.sold.model.UserModel;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.screens.forget_password.view.ForgetPasswordActivity;
import com.appabilities.sold.screens.home.view.HomeActivity;
import com.appabilities.sold.screens.login.LoginContract;
import com.appabilities.sold.screens.login.LoginPresenter;
import com.appabilities.sold.screens.registration.view.RegistrationActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.Config;
import com.appabilities.sold.utils.MyBitmapUtils;
import com.appabilities.sold.utils.NotificationUtils;
import com.appabilities.sold.utils.SnackUtils;
import com.iamhabib.easy_preference.EasyPreference;

import org.androidannotations.annotations.App;

public class LoginActivity extends BaseActivity implements LoginContract.View<LoginResponse> {

    ActivityLoginBinding bi;
    LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_login);
        bi.setView(this);
        loginPresenter = new LoginPresenter(this);
        bi.setPresenter(loginPresenter);
        loginPresenter.initScreen();
    }

    @Override
    public void setupViews() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ca.getResourceColor(R.color.colorPrimaryDark));
        }
    }



    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onLoading() {
        bi.btnLogin.startAnimation();
    }

    @Override
    public void onRequestSuccessful(LoginResponse loginResponse) {
        loginResponse.localID = 1;
        loginResponse.save();

        // Followers
        for (int kk = 0; kk < loginResponse.followers_detail.size(); kk++) {

            UserModel temp = loginResponse.followers_detail.get(kk);
            temp.type = AppConstants.USER_TYPE.FOLLOWER;
            temp.save();

            RelationshipModel relation = new RelationshipModel();
            relation.followerId = temp.userID;
            relation.followingId = loginResponse.userID;
            relation.relationshipId = relation.followerId + "" + relation.followingId;
            relation.textValue = temp.display_name + " --> " + loginResponse.display_name;
            relation.save();
            temp.save();


        }

        // Following
        for (int kk = 0; kk < loginResponse.following_detail.size(); kk++) {

            UserModel temp = loginResponse.following_detail.get(kk);
            temp.type = AppConstants.USER_TYPE.FOLLOWING;
            temp.save();

            RelationshipModel relation = new RelationshipModel();
            relation.followerId = loginResponse.userID;
            relation.followingId = temp.userID;
            relation.relationshipId = relation.followerId + "" + relation.followingId;
            relation.textValue = loginResponse.display_name + " --> " + temp.display_name;
            relation.save();
        }
        Bitmap icon = MyBitmapUtils.getBitmapFromVectorDrawable(this, R.drawable.ic_check_black_24dp);
        bi.btnLogin.doneLoadingAnimation(getResources().getColor(android.R.color.white), icon);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                Intent ii = new Intent(LoginActivity.this, HomeActivity.class);
                ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(ii);
                finish();
            }
        }, 800);
    }

    @Override
    public void onRequestFail(String errorMessage) {
        SnackUtils.showSnackMessage(this, errorMessage);
        Bitmap icon = MyBitmapUtils.getBitmapFromVectorDrawable(this, R.drawable.ic_clear_black_24dp);
        bi.btnLogin.doneLoadingAnimation(getResources().getColor(android.R.color.holo_red_dark), icon);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                bi.btnLogin.revertAnimation();
            }
        }, 800);
    }

    @Override
    public void onLoginClick() {
        ca.hideKeyboard(this);
        loginPresenter.onLoginButtonClick(bi.txtUsername.getText().toString(),
                bi.txtPassword.getText().toString().trim());
    }

    @Override
    public void onResetPasswordClick() {
        startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
    }

    @Override
    public void onRegisterHereClick() {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }

    @Override
    public void showUsernameError() {
        bi.txtUsername.setError("Input Username");
        bi.txtUsername.requestFocus();
    }

    @Override
    public void showPasswordError() {
        bi.txtPassword.setError("Input Password");
        bi.txtPassword.requestFocus();
    }

    @Override
    public Context getContext() {
        return this;
    }
}
