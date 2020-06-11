package com.appabilities.sold.screens.request.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.appabilities.sold.R;
import com.appabilities.sold.base.sidemenu.SideMenuBaseActivity;
import com.appabilities.sold.databinding.ActivityRequestBinding;
import com.appabilities.sold.screens.add_request.view.AddRequestActivity;
import com.appabilities.sold.screens.request.RequestContract;
import com.appabilities.sold.screens.request.RequestPresenter;
import com.appabilities.sold.screens.request.view.fragment.my_request.view.MyRequestFragment;
import com.appabilities.sold.screens.request.view.fragment.user_request.view.UserRequestFragment;

import java.util.ArrayList;
import java.util.List;

public class RequestActivity extends SideMenuBaseActivity implements RequestContract.View {

    RequestPresenter presenter;
    ActivityRequestBinding bi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = (ActivityRequestBinding) setContentLayout(R.layout.activity_request);
        bi.setView(this);
        presenter = new RequestPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();

    }

    @Override
    public void setupViews() {
        initDrawer(bi.toolbar, "REQUEST");
        setUpViewpager();
        bi.tabs.setupWithViewPager(bi.viewpager);
    }

    @Override
    public void setUpViewpager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyRequestFragment(), "My Request");
        adapter.addFragment(new UserRequestFragment(), "Requests");
        bi.viewpager.setAdapter(adapter);
    }

    @Override
    public void onClickAddRequest() {
        startActivity(new Intent(RequestActivity.this, AddRequestActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        selectMenuOption(R.id.nav_my_request);
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
