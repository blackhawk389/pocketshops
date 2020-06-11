package com.appabilities.sold.screens.home.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.appabilities.sold.R;
import com.appabilities.sold.base.sidemenu.SideMenuBaseActivity;
import com.appabilities.sold.databinding.ActivityHomeBinding;
import com.appabilities.sold.model.SearchModel;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.screens.add_advertisement.view.AddAdvertisementActivity;
import com.appabilities.sold.screens.add_auction.view.AddAuctionActivity;
import com.appabilities.sold.screens.add_product.view.AddProductActivity;
import com.appabilities.sold.screens.add_request.view.AddRequestActivity;
import com.appabilities.sold.screens.chat_with_seller.chat_activity.view.ChatWithSellerActivity;
import com.appabilities.sold.screens.home.HomeContract;
import com.appabilities.sold.screens.home.HomePresenter;
import com.appabilities.sold.screens.home.SearchPopupInterface;
import com.appabilities.sold.screens.home.fragment.buy.view.BuyFragment;
import com.appabilities.sold.screens.home.fragment.categories.view.CategoriesFragment;
import com.appabilities.sold.screens.home.fragment.favourites.view.FavouriteFragment;
import com.appabilities.sold.screens.home.fragment.sell.view.SellFragment;
import com.appabilities.sold.screens.show_products.view.ShowProductsActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.CustomViewPager;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends SideMenuBaseActivity implements HomeContract.View, SearchPopupInterface {

    ActivityHomeBinding bi;
    HomePresenter presenter;
    SearchPopupView searchPopupView;
    private String TAG = "searchData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = (ActivityHomeBinding) setContentLayout(R.layout.activity_home);
        bi.setView(this);
        presenter = new HomePresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();

        SendBird.connect(loginResponse.userID, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {    // Error.
                    Log.d("tag", "SendBirdException: " + e.getMessage());
                    return;
                }

                if(getIntent().getExtras() != null){
                    Intent intent = new Intent(HomeActivity.this, ChatWithSellerActivity.class);
                    if(getIntent().getExtras().getBundle("chatBundle") != null){
                        if(getIntent().getExtras().getBundle("chatBundle").getString("channel_url") != null){
                            intent.putExtra("chatBundle", getIntent().getExtras().getBundle("chatBundle"));
                        }
                    }
                    startActivity(intent);
                }
                Log.d("tag", "SendBirdUser: " + user.getUserId());

                /*String device_token = EasyPreference.with(HomeActivity.this).getString(AppConstants.KEYS.REG_ID.name(), null);

                if (device_token == null) return;

                SendBird.registerPushTokenForCurrentUser(device_token, new SendBird.RegisterPushTokenWithStatusHandler() {
                    @Override
                    public void onRegistered(SendBird.PushTokenRegistrationStatus status, SendBirdException e) {
                        if (e != null) {    // Error.
                            return;
                        }
                    }
                });*/
            }
        });
    }

    @Override
    public void setupViews() {
        initDrawer(bi.toolbar, "HOME");
        setupViewPager(bi.viewpagerHome);
        bi.tabsHome.setupWithViewPager(bi.viewpagerHome);
    }

    @Override
    public void onSearchClick() {
        searchPopupView = new SearchPopupView(this, R.layout.search_dialog_layout, bi.txtSearch);
        searchPopupView.setSearchListener(this);
        searchPopupView.show();

        ca.hideKeyboard(this);
    }

    @Override
    public void onInviteClick() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at: https://play.google.com/store/apps/details?id=com.appabilities.sold");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public void onClickAddAuction() {
        bi.floatActionMenu.close(true);
        startActivity(new Intent(HomeActivity.this, AddAuctionActivity.class));
    }

    @Override
    public void onClickAddProduct() {
        bi.floatActionMenu.close(true);
        startActivity(new Intent(HomeActivity.this, AddProductActivity.class));
    }

    @Override
    public void onClickAddRequest() {
        bi.floatActionMenu.close(true);
        startActivity(new Intent(HomeActivity.this, AddRequestActivity.class));
    }

    @Override
    public void onClickAddAdvertisement() {
        bi.floatActionMenu.close(true);
        startActivity(new Intent(HomeActivity.this, AddAdvertisementActivity.class));
    }

    private void setupViewPager(CustomViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BuyFragment(), "BUY");
        adapter.addFragment(new SellFragment(), "SELL");
        adapter.addFragment(new FavouriteFragment(), "FAVOURITES");
        adapter.addFragment(new CategoriesFragment(), "CATEGORIES");
        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        selectMenuOption(R.id.nav_home);
    }

    @Override
    public void OnSearchClicked(SearchModel data) {
        Intent intent = new Intent(HomeActivity.this, ShowProductsActivity.class);
        intent.putExtra(AppConstants.KEYS.SEARCH_MODEL.name(), Parcels.wrap(data));
        intent.putExtra(AppConstants.KEYS.IS_FROM_CATEGORY.name(), false);
        startActivity(intent);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                if (searchPopupView != null && searchPopupView.isOpened()) {
                    //super.onBackPressed();
                    searchPopupView.close(true);
                    return false;
                }

                if (bi.floatActionMenu.isOpened())
                    bi.floatActionMenu.close(true);
                else
                    finish();
                break;
        }
        return false;
    }

    public LoginResponse getLoginResponse(){
        return loginResponse;
    }


}
