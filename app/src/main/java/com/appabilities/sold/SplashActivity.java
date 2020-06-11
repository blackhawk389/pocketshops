package com.appabilities.sold;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.networking.response.LoginResponse_Table;
import com.appabilities.sold.screens.home.view.HomeActivity;
import com.appabilities.sold.screens.login.view.LoginActivity;
import com.appabilities.sold.screens.my_account.view.MyAccountActivity;
import com.appabilities.sold.screens.my_orders.view.MyOrderActivity;
import com.appabilities.sold.screens.my_sale.view.MySaleActivity;
import com.raizlabs.android.dbflow.sql.language.Select;

public class SplashActivity extends BaseActivity {

    private static long SLEEP_TIME = 2000;
    public static String TAG = SplashActivity.class.getSimpleName();
    String channel_url, seller_id, seller_name, username, product_id, product_title  = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ca.getResourceColor(R.color.colorPrimaryDark));
        }

        // Start timer and launch main activity
        IntentLauncher launcher = new IntentLauncher();
        launcher.start();


        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }


    private class IntentLauncher extends Thread {
        @Override
        /**
         * Sleep for some time and than start new activity.
         */
        public void run() {
            try {
                // Sleeping
                Thread.sleep(SLEEP_TIME);
            } catch (Exception e) {
                //Log.e(TAG, e.getMessage());
            }

            // Detect Session
            LoginResponse logins = new Select().from(LoginResponse.class).where(LoginResponse_Table.localID.is(1)).querySingle();
            if (logins != null)
            {
                // Session is valid
                // Start main activity
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                if(getIntent().getExtras() != null){
                    if(getIntent().getExtras().getString("channel_url") != null){
                        intent = new Intent(SplashActivity.this, HomeActivity.class);
                        intent.putExtra("chatBundle", getIntent().getExtras());
                    }
                    if(getIntent().getExtras().getString("openFragment") != null){
                        String payload = getIntent().getExtras().getString("openFragment");
                        if(payload.equalsIgnoreCase("payment")){
                            intent = new Intent(SplashActivity.this, MyAccountActivity.class);
                            intent.putExtra("from_notification", true);
                        }
                        if(payload.equalsIgnoreCase("orders")){
                            intent = new Intent(SplashActivity.this, MySaleActivity.class);
                            intent.putExtra("from_notification", true);
                        }
                    }
                }
                else {
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                }
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
            else
            {
                // Invalid Session, Show login
                // Start main activity
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }


        }
    }
}

