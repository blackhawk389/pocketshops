package com.appabilities.sold.screens.payment_webview;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.database.tables.CartItemModel;
import com.appabilities.sold.database.tables.CartItemModel_Table;
import com.appabilities.sold.databinding.ActivityOnlinePaymentBinding;
import com.appabilities.sold.model.OrderDetailModel;
import com.appabilities.sold.model.OrderModel;
import com.appabilities.sold.screens.confirm_order.view.ConfirmOrderActivity;
import com.appabilities.sold.screens.home.view.HomeActivity;
import com.appabilities.sold.screens.my_orders.view.MyOrderActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.kennyc.view.MultiStateView;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.parceler.Parcels;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class OnlinePaymentActivity extends BaseActivity implements OnlinePaymentContract.View {
    OnlinePaymentPresenter presenter;
    ActivityOnlinePaymentBinding binding;
    private String checkoutId = "";
    private String postData = "";
    private String WEB_PAYMENT_URL = "";
    private OrderModel orderModel;
    private String advertRID;
    private String advert_no_days;
    private String productID;
    private String rate;
    private String totalAmount;
    private String url;
    private String title;
    private String desc;
    private File advertImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_online_payment);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        presenter = new OnlinePaymentPresenter(this);
        presenter.initScreen();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;

    }

    @Override
    public void setupViews() {
        getSupportActionBar().setTitle("Payment");
        checkoutId = getIntent().getStringExtra("checkoutId");

        advertRID = getIntent().getStringExtra("advertRID");
        advert_no_days = getIntent().getStringExtra("advert_no_days");
        rate = getIntent().getStringExtra("rate");
        productID = getIntent().getStringExtra("productID");
        totalAmount = getIntent().getStringExtra("totalAmount");
        advertImg = getIntent().getParcelableExtra("advertImg");
        url = getIntent().getParcelableExtra("url");
        title = getIntent().getParcelableExtra("title");
        desc = getIntent().getParcelableExtra("desc");

        orderModel = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.ORDER_ITEM.name()));
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.wv, true);
        initWebView(checkoutId);
    }

    @Override
    public void successfullySubmitOrder() {
        for (OrderDetailModel item: orderModel.getOrderDetails()) {
            deleteShoppingCartItem(item.getProductID());
        }
        SnackUtils.showSnackMessage(this, "Order Submitted Successfully");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =new Intent(OnlinePaymentActivity.this, MyOrderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(AppConstants.KEYS.IS_REFRESH.name(), true);
                startActivity(intent);
                finish();
            }
        }, 700);
    }

    @Override
    public void successfullySubmitAdvertise() {
        SnackUtils.showSnackMessage(this, "Advertisement Submitted Successfully");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =new Intent(OnlinePaymentActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, 700);
    }

    @Override
    public void errorSubmitOrder() {
        SnackUtils.showSnackMessage(this, "Payment Decline");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 700);
    }

    @Override
    public void deleteShoppingCartItem(String productID) {
        SQLite.delete(CartItemModel.class)
                .where(CartItemModel_Table.productId.is(productID))
                .async()
                .execute();
    }

    private void initWebView(String checkoutId){
        //WEB_PAYMENT_URL = "https://pocket-shops.com/payment.php?id="+checkoutId;
        WEB_PAYMENT_URL = "https://pocket-shops.com/payment-mobile.php?id="+checkoutId;
        //postData = "amount=" + URLEncoder.encode(final_price, "UTF-8") +

        binding.wv.setWebViewClient(new MyWebViewClient());
        binding.wv.getSettings().setJavaScriptEnabled(true);

        binding.wv.loadUrl(WEB_PAYMENT_URL);
        //binding.wv.postUrl(WEB_PAYMENT_URL, postData.getBytes());
        //class used to handle page content and get JSON Data.
        binding.wv.addJavascriptInterface(new SuccessJavaScriptInterface(this), "HtmlViewer");
    }


    private class SuccessJavaScriptInterface {
        private Context ctx;

        SuccessJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void getJson(String jsonStr) {
            //get page content using JavaScript and send it to main page
            //System.out.println(jsonStr);
            Log.d("tag", "getJson called: "+ jsonStr);
            String status = jsonStr.toLowerCase();
            if(status.equalsIgnoreCase("Payment Successful")){
                if(orderModel == null){
                    presenter.submitAdvertisement(loginResponse.access_token,
                            advertRID,
                            advert_no_days,
                            rate,
                            productID,
                            totalAmount,
                            advertImg,
                            url,
                            title,
                            desc);
                }
                else {
                    presenter.placeOrder(orderModel);
                }
            }
            else if(status.equalsIgnoreCase("Payment Declined")){
                SnackUtils.showSnackMessage((BaseActivity)ctx, "Payment Decline");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 700);
            }
            /*if (callBackPress) {
                setResult(RESULT_CANCELED);
                finish();
            } else {
                //paymentResult.onPaymentResponse(jsonStr);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", jsonStr);
                Log.i(TAG, "getJson: " + jsonStr);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

            }*/

        }
    }


    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {
            //ignore ssl error
            //handler.proceed();
            final AlertDialog.Builder builder = new AlertDialog.Builder(OnlinePaymentActivity.this);
            String message = "SSL Certificate error.";
            switch (error.getPrimaryError()) {
                case SslError.SSL_UNTRUSTED:
                    message = "The certificate authority is not trusted.";
                    break;
                case SslError.SSL_EXPIRED:
                    message = "The certificate has expired.";
                    break;
                case SslError.SSL_IDMISMATCH:
                    message = "The certificate Hostname mismatch.";
                    break;
                case SslError.SSL_NOTYETVALID:
                    message = "The certificate is not yet valid.";
                    break;
            }
            message += " Do you want to continue anyway?";

            builder.setTitle("SSL Certificate Error");
            builder.setMessage(message);
            builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("tag", "onPageFinished: " + url);
            Log.d("tag", "onPageFinished: ");

            String releasePage = "thanks-mobile.php".toLowerCase();
            if (url.toLowerCase().contains(releasePage)) {
                binding.wv.loadUrl("javascript:HtmlViewer.getJson" +
                        "(document.getElementsByTagName('body')[0].innerHTML);");

            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Log.d("tag", "onReceivedError: ");
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.d("tag", "onReceivedError: " + failingUrl);
            Log.d("tag", "errorcode: "+String.valueOf(errorCode)+" -->"+description);

        }
    }

}
