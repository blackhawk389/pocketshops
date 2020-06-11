package com.appabilities.sold.base.sidemenu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.drawerlayout.widget.DrawerLayout;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.custom.CustomNavigationView;
import com.appabilities.sold.database.SoldDatabase;
import com.appabilities.sold.screens.advertisement.view.AdvertisementActivity;
import com.appabilities.sold.screens.auction.view.MyAuctionActivity;
import com.appabilities.sold.screens.categories.view.CategoriesActivity;
import com.appabilities.sold.screens.chat_with_seller.chat_list.ChatListActivity;
import com.appabilities.sold.screens.home.view.HomeActivity;
import com.appabilities.sold.screens.login.view.LoginActivity;
import com.appabilities.sold.screens.my_account.view.MyAccountActivity;
import com.appabilities.sold.screens.my_orders.view.MyOrderActivity;
import com.appabilities.sold.screens.my_products.view.MyProductsActivity;
import com.appabilities.sold.screens.my_sale.view.MySaleActivity;
import com.appabilities.sold.screens.privacy_policy.PrivacyPolicyActivity;
import com.appabilities.sold.screens.request.view.RequestActivity;
import com.appabilities.sold.screens.search_seller.view.SearchSellerActivity;
import com.appabilities.sold.screens.settings.view.SettingsActivity;
import com.appabilities.sold.screens.user_profile.view.UserProfileActivity;
import com.appabilities.sold.utils.AppConstants;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.navigation.NavigationView;
import com.iamhabib.easy_preference.EasyPreference;

public class SideMenuBaseActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        SideMenuBaseContract.View {

    protected Toolbar toolbar;
    protected FrameLayout contentLayout;
    protected DrawerLayout drawer;
    protected NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    private CustomNavigationView itemsNavView;
    private NavigationView footerNavView;
    protected TextView txtEmail, txtName, privacy;
    protected SimpleDraweeView userImage;

    SideMenuBasePresenter _presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_menu_base);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contentLayout = (FrameLayout) findViewById(R.id.content_layout);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        itemsNavView = (CustomNavigationView) navigationView.findViewById(R.id.nav_view_items);
        View headerLayout = itemsNavView.getHeaderView(0);
        footerNavView = (NavigationView) navigationView.findViewById(R.id.nav_view_footer);
        privacy = footerNavView.findViewById(R.id.footer_item_2);
        itemsNavView.setNavigationItemSelectedListener(this);
        footerNavView.setNavigationItemSelectedListener(this);

        txtName = (TextView) headerLayout.findViewById(R.id.txtUsername_navmenu);
        txtEmail = (TextView) headerLayout.findViewById(R.id.txtEmail_navmenu);
        userImage = (SimpleDraweeView) headerLayout.findViewById(R.id.imgProfile_navmenu);
        userImage.setImageURI(loginResponse.avatar);

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SideMenuBaseActivity.this, PrivacyPolicyActivity.class);
                intent.putExtra(AppConstants.KEYS.PRIVACY_POLICY.name(), AppConstants.KEYS.PRIVACY_POLICY.name());
                startActivity(intent);
            }
        });

        /*userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SideMenuBaseActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });*/

        _presenter = new SideMenuBasePresenter(this);
        _presenter.updateHeaderData(loginResponse);
    }

    public void initDrawer(Toolbar tbar, String title)
    {
        toolbar = tbar;

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toggle.syncState();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void setDrawerState(boolean isEnabled) {
        if ( isEnabled ) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.syncState();

        }
        else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toggle.syncState();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        int previousId = EasyPreference.with(this).getInt("selectedNav", R.id.nav_home);

        // If same button pressed, return
        if (previousId == id)
            return false;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_home)
        {
            // Home
            finish();
            Intent ii = new Intent(this, HomeActivity.class);
            startActivity(ii);
        }
        else if (id == R.id.nav_categories)
        {
            // Explore Activity
            if (previousId != R.id.nav_home)
                finish();

            launchCategoriesActivity();
        }
        else if (id == R.id.nav_my_account)
        {
            // Favourites
            if (previousId != R.id.nav_home)
                finish();

            launchMyProfileActivity();
        }
        else if (id == R.id.nav_my_products)
        {
            // Bookings
            if (previousId != R.id.nav_home)
                finish();

            launchMyProductsActivity();
        }
        else if (id == R.id.nav_my_orders)
        {
            // Bookings
            if (previousId != R.id.nav_home)
                finish();

            launchMyOrdersActivity();
        }
        else if (id == R.id.nav_ad_history)
        {
            // Reviews
            if (previousId != R.id.nav_home)
                finish();

            launchAdHistoryActivity();
        }
        else if (id == R.id.nav_my_sale)
        {
            // Profile
            // Settings
            if (previousId != R.id.nav_home)
                finish();

            launchMySaleActivity();
        }
        else if (id == R.id.nav_my_request)
        {
            // Profile
            // Settings
            if (previousId != R.id.nav_home)
                finish();

            launchMyRequestActivity();
        }
        else if (id == R.id.nav_auctions)
        {
            // Profile
            // Settings
            if (previousId != R.id.nav_home)
                finish();

            launchMyAuctionsActivity();
        }
        else if (id == R.id.nav_my_revenue)
        {
            // Favourites
            if (previousId != R.id.nav_home)
                finish();

            launchMyAccountActivity();
        }
        else if (id == R.id.nav_settings)
        {
            // Settings
            if (previousId != R.id.nav_home)
                finish();

            launchSettingsActivity();
        }
        else if (id == R.id.nav_seller)
        {
            // Settings
            if (previousId != R.id.nav_home)
                finish();

            launchSellerActivity();
        }
        else if (id == R.id.nav_logout)
        {
            // Settings
            if (previousId != R.id.nav_home)
                finish();

            callLogoutUser();
        }
        else if (id == R.id.nav_chat)
        {
            // Settings
            if (previousId != R.id.nav_home)
                finish();

            launchChatListActivity();
        }

        EasyPreference.with(this).addInt("selectedNav", id).save();

        return true;
    }




    public ViewDataBinding setContentLayout(int layoutID)
    {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View v = inflater.inflate(layoutID, contentLayout, true);
        return DataBindingUtil.inflate(inflater, layoutID, contentLayout, true);
    }

    @Override
    public void launchCategoriesActivity() {
        Intent ii = new Intent(this, CategoriesActivity.class);
        startActivity(ii);
    }

    @Override
    public void launchMyAccountActivity() {
        Intent ii = new Intent(this, MyAccountActivity.class);
        startActivity(ii);
    }

    @Override
    public void launchMyProfileActivity() {
        Intent intent = new Intent(SideMenuBaseActivity.this, UserProfileActivity.class);
        startActivity(intent);
    }

    public void launchMyProductsActivity() {
        Intent ii = new Intent(this, MyProductsActivity.class);
        startActivity(ii);
    }

    @Override
    public void launchAdHistoryActivity() {
        Intent ii = new Intent(this, AdvertisementActivity.class);
        startActivity(ii);
    }

    @Override
    public void launchMySaleActivity() {
        Intent ii = new Intent(this, MySaleActivity.class);
        startActivity(ii);
    }

    @Override
    public void launchSettingsActivity() {
        Intent ii = new Intent(this, SettingsActivity.class);
        startActivity(ii);
    }

    @Override
    public void launchMyOrdersActivity() {
        startActivity(new Intent(this, MyOrderActivity.class));
    }

    @Override
    public void launchMyAuctionsActivity() {
        startActivity(new Intent(this, MyAuctionActivity.class));
    }

    @Override
    public void launchMyRequestActivity() {
        startActivity(new Intent(this, RequestActivity.class));
    }

    @Override
    public void launchSellerActivity() {
        startActivity(new Intent(this, SearchSellerActivity.class));
    }

    @Override
    public void launchChatListActivity() {
        startActivity(new Intent(this, ChatListActivity.class));
    }

    private void callLogoutUser() {
        SoldDatabase.deleteDatabase();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void updateHeader(String name, String email, String userImg) {
        txtName.setText(name);
        txtEmail.setText(email);
//        userImage.setImageURI(userImg);
    }

    public void selectMenuOption(int itemID)
    {
        itemsNavView.setCheckedItem(itemID);
        EasyPreference.with(this).addInt("selectedNav", itemID).save();
    }


}
