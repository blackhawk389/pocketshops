package com.appabilities.sold.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.appabilities.sold.R;
import com.google.android.material.navigation.NavigationView;


/**
 * Created by Wajahat on 11/17/2016.
 */

public class CustomNavigationView extends NavigationView {

    public View headerView;

    public CustomNavigationView(Context context) {
        super(context);
    }


    public CustomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public CustomNavigationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    // Consumes touch in the NavigationView so it doesn't propagate to views below
    public boolean onTouchEvent(MotionEvent me) {
        return true;
    }


    // Inflates header as a child of NavigationView
    public void createHeader(int res) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        headerView = inflater.inflate(res, this, false);

        // Consumes touch in the header so it doesn't propagate to menu items below
        headerView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        addView(headerView);
    }


    // Positions and sizes the menu view
    public void sizeMenu(View view) {
        // Height of header
        int header_height = (int) getResources().getDimension(R.dimen.nav_header_height);

        // Gets required display metrics
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screen_height = displayMetrics.heightPixels;

        // Height of menu
        int menu_height = (int) (screen_height - header_height);

        // Layout params for menu
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        params.height = menu_height;
        view.setLayoutParams(params);
    }
}
