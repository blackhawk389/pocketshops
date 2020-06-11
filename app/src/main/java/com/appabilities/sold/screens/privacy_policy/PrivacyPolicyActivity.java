package com.appabilities.sold.screens.privacy_policy;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.databinding.DataBindingUtil;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivityPrivacyPolicyBinding;
import com.appabilities.sold.utils.AppConstants;

import org.androidannotations.annotations.App;
import org.w3c.dom.Text;

public class PrivacyPolicyActivity extends BaseActivity {
    ActivityPrivacyPolicyBinding binding;
    int ERROR_CODE = 0;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_privacy_policy);

        WebView myWebView = (WebView) findViewById(R.id.webview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(!TextUtils.isEmpty(getIntent().getStringExtra(AppConstants.KEYS.PRIVACY_POLICY.name()))){
            url = AppConstants.VALUES.PRIVACY_POLICY;
            getSupportActionBar().setTitle("Privacy Policy");
        }
        else if(!TextUtils.isEmpty(getIntent().getStringExtra(AppConstants.KEYS.REFUND_POLICY.name()))){
            url = AppConstants.VALUES.REFUND_POLICY;
            getSupportActionBar().setTitle("Refund Policy");
        }
        else if(!TextUtils.isEmpty(getIntent().getStringExtra(AppConstants.KEYS.TERMS_CONDITIONS.name()))){
            url = AppConstants.VALUES.TERMS_CONDITIONS;
            getSupportActionBar().setTitle("Terms And Conditions");
        }

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(url);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                binding.webview.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (ERROR_CODE == 0) {
                    binding.webview.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.INVISIBLE);
                } else {
                    binding.webview.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                binding.webview.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
                binding.txtError.setVisibility(View.VISIBLE);
                ERROR_CODE = error.getErrorCode();
                super.onReceivedError(view, request, error);
            }


        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
