package com.appabilities.sold.base;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.fragment.app.Fragment;

import com.appabilities.sold.utils.CommonActions;
import com.appabilities.sold.utils.NetworkUtils;

import java.lang.reflect.Field;
import java.util.Observable;

public abstract class BaseFragment extends Fragment implements IConectivityObserver {

    public BaseActivity mActivity;
    public CommonActions ca;
    public boolean isNetworkAvailable;

    @CallSuper
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = (BaseActivity) this.getActivity();
        ca = new CommonActions(mActivity);
        updateNetworkStatus();
    }

    public boolean onFragmentBackPressed() {
        return false;
    }

    public void updateNetworkStatus() {
        isNetworkAvailable = NetworkUtils.checkConnection(mActivity);
    }

    @Override
    public void onPause() {
        super.onPause();
        NetworkChangeReceiver.getObservable().deleteObserver(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkChangeReceiver.getObservable().addObserver(this);
        updateNetworkStatus();
    }

    @Override
    public void update(Observable observable, Object data) {
        updateNetworkStatus();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
