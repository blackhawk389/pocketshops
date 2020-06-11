package com.appabilities.sold.screens.seller_profile.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivitySellerProfileBinding;
import com.appabilities.sold.model.UserModel;
import com.appabilities.sold.networking.response.SellerProfileResponse;
import com.appabilities.sold.networking.response.VerifyUserResponse;
import com.appabilities.sold.screens.chat_with_seller.chat_activity.view.ChatWithSellerActivity;
import com.appabilities.sold.screens.seller_profile.SellerProfileContract;
import com.appabilities.sold.screens.seller_profile.SellerProfilePresenter;
import com.appabilities.sold.screens.seller_profile.fragment.product_grid.view.ProductGridFragment;
import com.appabilities.sold.screens.seller_profile.fragment.product_list.view.ProductListFragment;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class SellerProfileActivity extends BaseActivity
        implements SellerProfileContract.View, ViewPager.OnPageChangeListener {

    ActivitySellerProfileBinding bi;
    SellerProfilePresenter presenter;
    String sellerID = "";
    boolean isFollowing = false;
    SellerProfileResponse mSellerResponse;
    ViewPagerAdapter viewPagerAdapter;

    private int[] tabIcons = {
            R.drawable.ic_grid_item,
            R.drawable.ic_list_item
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_seller_profile);
        bi.setView(this);
        presenter = new SellerProfilePresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }


    @Override
    public void setupViews() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        setSupportActionBar(bi.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bi.collapsingToolbar.setExpandedTitleColor(ca.getResourceColor(android.R.color.transparent));
        sellerID = getIntent().getStringExtra(AppConstants.KEYS.SELLER_ID.name());
        presenter.getProfile(loginResponse.access_token, sellerID);
    }

    @Override
    public void setUpViewpager(ViewPager viewPager) {
        viewPagerAdapter.clearList();
        viewPagerAdapter.addFragment(ProductGridFragment.newInstance(mSellerResponse), "GRID");
        viewPagerAdapter.addFragment(ProductListFragment.newInstance(mSellerResponse), "LIST");
        viewPager.setAdapter(viewPagerAdapter);
        bi.tabs.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);
        setUpTabIcons();
    }

    @Override
    public void setUpTabIcons() {
        bi.tabs.getTabAt(0).setIcon(tabIcons[0]);
        bi.tabs.getTabAt(1).setIcon(tabIcons[1]);
    }

    @Override
    public void updateProfile(SellerProfileResponse body) {
        mSellerResponse = body;
        bi.collapsingToolbar.setTitle(body.displayName);
        bi.productCount.setText(body.productCount);
        bi.followersCount.setText(body.totalFollowers);
        bi.followingCount.setText(body.totalFollowing);
        bi.imgCover.setImageURI(body.coverImage);
        if (body.isFollowing.equals("1")){
            bi.btnFollow.setText("Following");
            isFollowing = true;
        }
        bi.userImg.setImageURI(body.avatar);
        bi.userTitle.setText(body.displayName);
        bi.txtDescription.setText(body.description == null?"":body.description);
        setUpViewpager(bi.viewpager);
        bi.progress.setVisibility(View.GONE);
        bi.coordinator.setVisibility(View.VISIBLE);

        if(body.userID.equalsIgnoreCase(loginResponse.userID)){
            bi.btnFollow.setVisibility(View.GONE);
            bi.btnChat.setVisibility(View.GONE);
        }
    }

    @Override
    public void errorInGettingProfile() {
    }

    @Override
    public void onLoading() {
        bi.progress.setVisibility(View.VISIBLE);
        bi.coordinator.setVisibility(View.GONE);
    }

    @Override
    public void onClickFollow() {
        bi.btnFollow.setClickable(false);
        bi.progress.setVisibility(View.VISIBLE);
        bi.progress.bringToFront();
        if (isFollowing){
            presenter.updateFollowStatus(loginResponse.access_token,
                    AppConstants.VALUES.FOLLOWING,
                    mSellerResponse.userID,
                    AppConstants.VALUES.STATUS_UNFOLLOW);
        }else {
            presenter.updateFollowStatus(loginResponse.access_token,
                    AppConstants.VALUES.FOLLOWING,
                    mSellerResponse.userID,
                    AppConstants.VALUES.STATUS_FOLLOW);

        }
    }

    @Override
    public void onClickChat() {
        Intent intent = new Intent(this, ChatWithSellerActivity.class);
        intent.putExtra(AppConstants.KEYS.SELLER_ID.name(), Parcels.wrap(mSellerResponse));
        startActivity(intent);
    }

    @Override
    public void updateFollowStatus(VerifyUserResponse body) {
        bi.btnFollow.setClickable(true);
        bi.progress.setVisibility(View.GONE);
        //new SellerProfileResponse().saveFollowing(mSellerResponse);
        SnackUtils.showSnackMessage(SellerProfileActivity.this, "Status Updated Successfully");
        if (isFollowing){
            bi.btnFollow.setText("FOLLOW");
            if(bi.followersCount.getText().toString().equalsIgnoreCase("0")){
                bi.followersCount.setText("0");
            }else{
                bi.followersCount.setText(Integer.parseInt(bi.followersCount.getText().toString()) - 1 + "");
            }

            isFollowing = false;
        } else{
            bi.btnFollow.setText("FOLLOWING");
            bi.followersCount.setText(Integer.parseInt(bi.followersCount.getText().toString()) + 1 + "");
            isFollowing = true;
        }
    }

    @Override
    public void errorFollowStatus() {
        bi.progress.setVisibility(View.GONE);
        SnackUtils.showSnackMessage(SellerProfileActivity.this, "Error in updating status");
        bi.btnFollow.setClickable(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
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
            return null;
        }

        public void clearList() {
            if (mFragmentList != null && mFragmentTitleList != null) {
                mFragmentList.clear();
                mFragmentTitleList.clear();
            }
        }
    }

}
