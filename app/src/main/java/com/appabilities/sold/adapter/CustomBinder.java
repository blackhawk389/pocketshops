package com.appabilities.sold.adapter;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.appabilities.sold.R;

/**
 * Created by Admin on 11/22/2019.
 */

public class CustomBinder {

    @BindingAdapter("android:percentage")
    public static void text(TextView textView, String percentage) {
        if((percentage == null || percentage.equalsIgnoreCase(""))){
            textView.setText("00");
        }else{
            textView.setText(percentage);
        }

    }
}
