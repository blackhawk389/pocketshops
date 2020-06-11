package com.appabilities.sold.base;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.IntentCompat;

import com.appabilities.sold.MyApp;
import com.appabilities.sold.R;
import com.appabilities.sold.database.SoldDatabase;
import com.appabilities.sold.database.tables.CartItemModel;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.screens.login.view.LoginActivity;
import com.appabilities.sold.screens.shopping_cart.view.ShoppingCartActivity;
import com.appabilities.sold.utils.CommonActions;
import com.appabilities.sold.utils.NetworkUtils;
import com.appabilities.sold.utils.SnackUtils;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.github.mrengineer13.snackbar.SnackBar;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Observable;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends AppCompatActivity implements IConectivityObserver{

    public CommonActions ca;
    public boolean isNetworkAvailable;
    public LoginResponse loginResponse;
    public int cartItemsCount = 0;
    public static boolean isChanged;
    public MenuItem cartMenuItem;

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean changed) {
        isChanged = changed;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // inside your activity (if you did not enable transitions in your theme)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        super.onCreate(savedInstanceState);
        setSharedElemts();
        ca = new CommonActions(this);
        updateNetworkStatus();
        loginResponse = SQLite.select().from(LoginResponse.class).querySingle();

        if (loginResponse != null)
            Log.i("login", "onCreate: "+loginResponse.toString());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void updateNetworkStatus() {
        isNetworkAvailable = NetworkUtils.checkConnection(this);
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
        refreshCount();
        MyApp.setCurrentActivity(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        cartMenuItem = menu.findItem(R.id.menuShoppingCart_home);
        ActionItemBadge.update(this, cartMenuItem, FontAwesome.Icon.faw_shopping_cart, ActionItemBadge.BadgeStyles.RED.getStyle(), cartItemsCount);

        //If you want to add your ActionItem programmatically you can do this too. You do the following:
        //new ActionItemBadgeAdder().act(this).menu(menu).title(R.string.sample_2).itemDetails(0, SAMPLE2_ID, 1).showAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS).add(bigStyle, 1);

        return true;

    }

    public void refreshCount()
    {
        cartItemsCount = (int) SQLite.select().from(CartItemModel.class).queryList().size();
        ActionItemBadge.update(this, cartMenuItem, FontAwesome.Icon.faw_shopping_cart, ActionItemBadge.BadgeStyles.RED.getStyle(), cartItemsCount);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == android.R.id.home)
        {
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP)
            {
                supportFinishAfterTransition();
            }
            else {
                onBackPressed();
            }

            return true;
        }else if (item.getItemId() == R.id.menuShoppingCart_home){
            startActivity(new Intent(this, ShoppingCartActivity.class));
        }
        return false;
    }

    protected void setupToolbar(Toolbar toolbar, String title) {
        toolbar.setTitle("");
        TextView textView = (TextView)toolbar.findViewById(R.id.title);
        textView.setText(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setSharedElemts()
    {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP)
        {
            //getWindow().setSharedElementEnterTransition(DraweeTransform.createTransitionSet());

            getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(
                    ScalingUtils.ScaleType.FOCUS_CROP, ScalingUtils.ScaleType.FOCUS_CROP));
            getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(
                    ScalingUtils.ScaleType.FOCUS_CROP, ScalingUtils.ScaleType.FOCUS_CROP));

            /*
            getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(
                    ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.FOCUS_CROP));
            getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(
                    ScalingUtils.ScaleType.FOCUS_CROP, ScalingUtils.ScaleType.CENTER_CROP));
                    */

        }

    }

    protected AlertDialog createAlertDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    protected ProgressDialog createProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        return progressDialog;
    }

    protected void setupActionBar(Toolbar toolbar) {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setLogo(ca.getResourceDrawable(R.drawable.md_btn_selected));
    }

    public void changeStatusBarColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ca.getResourceColor(R.color.colorPrimaryDark));
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        updateNetworkStatus();
    }

    public void logoutApp(){
        SoldDatabase.deleteDatabase();
        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
//        ComponentName cn = intent.getComponent();
//        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
        startActivity(intent);
    }
}
