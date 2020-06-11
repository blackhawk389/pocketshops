package com.appabilities.sold.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.appabilities.sold.MyApp;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommonActions {

    Activity currentActivity;

    public CommonActions(Activity activity) {
        this.currentActivity = activity;

    }

    public static enum KEYS{
    }


    public static String getTimeFromString(String time){
        DateFormat dateParse = new SimpleDateFormat("hh:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        try {
            Date date = dateParse.parse(time);
            String formattedDate = dateFormat.format(date);
            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getHourFromString(String time){
        DateFormat dateParse = new SimpleDateFormat("hh:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("hh");
        try {
            Date date = dateParse.parse(time);
            String formattedDate = dateFormat.format(date);
            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void savePreferences(String key, boolean value) {
        SharedPreferences sharedPreferences = currentActivity.getSharedPreferences(
                MyApp.APP_SETTINGS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public int getImageOrientation(String imagePath){
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public void clearAllPreferences() {
        SharedPreferences sharedPreferences = currentActivity.getSharedPreferences(
                MyApp.APP_SETTINGS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void saveUserPreferences(String key, String value) {
        SharedPreferences sharedPreferences = currentActivity.getSharedPreferences(
                MyApp.APP_SETTINGS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getValueString(String key, String default_value) {

        SharedPreferences sharedPreferences = currentActivity.getSharedPreferences(
                MyApp.APP_SETTINGS_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, default_value);
    }

    public boolean getValueBoolean(String key, boolean default_value) {

        SharedPreferences sharedPreferences = currentActivity.getSharedPreferences(
                MyApp.APP_SETTINGS_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, default_value);
    }

    public int getValueInt(String key, int default_value) {

        SharedPreferences sharedPreferences = currentActivity.getSharedPreferences(
                MyApp.APP_SETTINGS_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, default_value);
    }

    public void savePreferences(String key, int value) {
        SharedPreferences sharedPreferences = currentActivity.getSharedPreferences(
                MyApp.APP_SETTINGS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static String getDayFromString(int year, int month, int dayOfMonth) {
        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
        Date date = new Date(year, month, dayOfMonth - 1);
        String dayOfWeek = simpledateformat.format(date);
        return dayOfWeek;
    }

    public String getDayFromString(String input) {
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = inFormat.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        return outFormat.format(date);
    }

    public Date getDateFromString(String input) {
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate;
        try {
            startDate = inFormat.parse(input);
            return startDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isDutch(Context cxt){
        Locale currentLocale = cxt.getResources().getConfiguration().locale;
        if (currentLocale.equals(new Locale("nl"))) {
            return true;
        }
        return false;
    }


    public static void hideKeyboard(Context context) {
        InputMethodManager inputManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        View v = ((Activity) context).getCurrentFocus();
        if (v == null) {
            return;
        }

        inputManager
                .hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showKeyboard(Context context, EditText editText) {
        InputMethodManager inputManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        View v = ((Activity) context).getCurrentFocus();
        if (v == null) {
            return;
        }

        inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    // helper function to check if Maps is installed
    public boolean isAppInstalled(String uri) {
        PackageManager pm = currentActivity.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


    // Returns String from resources
    public String getResourceString(int rId) {

        return currentActivity.getResources().getString(rId);

    }

    // Returns String from resources
    public int getResourceColor(int rId) {

        return ContextCompat.getColor(currentActivity, rId);

    }

    // Returns Drawable from resources
    public Drawable getResourceDrawable(int rId) {

        return ContextCompat.getDrawable(currentActivity, rId);

    }

    public String getEditTextValue(EditText et) {

        return et.getText().toString().trim();

    }

    public boolean testEmpty(String str) {

        if (str == null || str.isEmpty()) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public long getAppVersion() {
        try {
            PackageInfo packageInfo = currentActivity.getPackageManager().getPackageInfo(
                    currentActivity.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

}
