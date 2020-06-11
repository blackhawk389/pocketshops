package com.appabilities.sold;

import android.app.Activity;
import android.app.Application;
import android.content.Context;


import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.sendbird.android.SendBird;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Hamza on 9/13/2017.
 */

public class MyApp extends MultiDexApplication {
    public static String APP_SETTINGS_FILE = "SOLD";
    private static Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        Stetho.initializeWithDefaults(this);
        FlowManager.init(new FlowConfig.Builder(this).build());

        SendBird.init("D314146F-7BF2-49DB-996D-0318E43CF905", this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/SF-UI-Display-Medium.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Activity getCurrentActivity(){
        return currentActivity;
    }
    public static void setCurrentActivity(Activity mCurrentActivity){
        currentActivity = mCurrentActivity;
    }
}
