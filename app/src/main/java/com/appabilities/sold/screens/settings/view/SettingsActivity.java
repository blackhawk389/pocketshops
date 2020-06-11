package com.appabilities.sold.screens.settings.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;

import com.appabilities.sold.R;
import com.appabilities.sold.base.sidemenu.SideMenuBaseActivity;
import com.appabilities.sold.databinding.ActivitySettingsBinding;
import com.appabilities.sold.screens.categories.view.CategoriesActivity;
import com.appabilities.sold.screens.change_password.view.ChangePasswordActivity;
import com.appabilities.sold.screens.privacy_policy.PrivacyPolicyActivity;
import com.appabilities.sold.screens.select_categories.view.SelectCategoriesActivity;
import com.appabilities.sold.screens.settings.SettingsContract;
import com.appabilities.sold.screens.settings.SettingsPresenter;
import com.appabilities.sold.utils.AppConstants;
import com.iamhabib.easy_preference.EasyPreference;

public class SettingsActivity extends SideMenuBaseActivity implements SettingsContract.View, CompoundButton.OnCheckedChangeListener {

    ActivitySettingsBinding bi;
    SettingsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = (ActivitySettingsBinding) setContentLayout(R.layout.activity_settings);
        bi.setView(this);
        presenter = new SettingsPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        initDrawer(bi.toolbar, "SETTINGS");
        bi.switchPush.setOnCheckedChangeListener(this);
        bi.switchPush.setChecked(true);
        int status = EasyPreference.with(this).getInt(AppConstants.KEYS.ON_NOTIFICATION.name(), 0);
        if (status == 1)
            bi.switchPush.setChecked(true);
    }

    @Override
    public void onClickChangeCategories() {
        Intent intent = new Intent(this, SelectCategoriesActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClickChangePassword() {
        startActivity(new Intent(SettingsActivity.this, ChangePasswordActivity.class));
    }

    @Override
    public void onClickRefundPolicy() {
        Intent intent = new Intent(this, PrivacyPolicyActivity.class);
        intent.putExtra(AppConstants.KEYS.REFUND_POLICY.name(), AppConstants.KEYS.REFUND_POLICY.name());
        startActivity(intent);
    }

    @Override
    public void onClickTermsConditions() {
        Intent intent = new Intent(this, PrivacyPolicyActivity.class);
        intent.putExtra(AppConstants.KEYS.TERMS_CONDITIONS.name(), AppConstants.KEYS.TERMS_CONDITIONS.name());
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
            EasyPreference.with(this).addInt(AppConstants.KEYS.ON_NOTIFICATION.name(), 1).save();
        else
            EasyPreference.with(this).addInt(AppConstants.KEYS.ON_NOTIFICATION.name(), 0).save();
    }

    @Override
    public void onResume() {
        super.onResume();
        selectMenuOption(R.id.nav_settings);
    }
}
