package com.appabilities.sold.utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.appabilities.sold.R;
import com.google.android.material.snackbar.Snackbar;
import com.tingyik90.snackprogressbar.SnackProgressBar;
import com.tingyik90.snackprogressbar.SnackProgressBarManager;

/**
 * Created by Wajahat Karim on 8/13/2017.
 */

public class SnackUtils {

    static SnackProgressBar snackProgressBar;
    static SnackProgressBarManager snackProgressBarManager;

    public static void showSnackMessage(Activity ctx, String message, boolean type)
    {
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) ctx.findViewById(android.R.id.content)).getChildAt(0);
        // No Internet Connection
        Snackbar snackbar = Snackbar.make(viewGroup, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(type==true? Color.parseColor("#1c8706"): Color.parseColor("#c7240b"));
        snackbar.show();
    }

    public static void showSnackMessage(Activity ctx, String message)
    {
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) ctx.findViewById(android.R.id.content)).getChildAt(0);
        // No Internet Connection
        Snackbar snackbar = Snackbar.make(viewGroup, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static void showLoadingSnack(Activity ctx, String message)
    {
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) ctx.findViewById(android.R.id.content)).getChildAt(0);

        if (snackProgressBar == null)
            snackProgressBar = new SnackProgressBar(SnackProgressBar.TYPE_INDETERMINATE, message);

        if (snackProgressBarManager == null)
            snackProgressBarManager = new SnackProgressBarManager(viewGroup)
                    .setProgressBarColor(R.color.colorAccent)
                    .setOverlayLayoutColor(android.R.color.black);

        snackProgressBarManager.show(snackProgressBar, SnackProgressBarManager.LENGTH_INDEFINITE);
    }

    public static Snackbar showLoadingSnackbar(Activity ctx, String message)
    {
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) ctx.findViewById(android.R.id.content)).getChildAt(0);
        Snackbar bar = Snackbar.make(viewGroup, message, Snackbar.LENGTH_INDEFINITE);
        //bar.getView().setBackgroundColor(Color.rgb(189,183,107));
        bar.show();
        return bar;
    }

    public static void showErrorMessage(Activity ctx, String message)
    {
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) ctx.findViewById(android.R.id.content)).getChildAt(0);
        // No Internet Connection
        Snackbar snackbar = Snackbar.make(viewGroup, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(Color.RED);
        snackbar.show();
    }

    public static void hideLoadingSnack()
    {
        if (snackProgressBar != null && snackProgressBarManager != null)
            snackProgressBarManager.dismissAll();

    }
}
