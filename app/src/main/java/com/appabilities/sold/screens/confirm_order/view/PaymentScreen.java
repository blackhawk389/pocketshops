package com.appabilities.sold.screens.confirm_order.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.SortedList;

import com.appabilities.sold.R;
import com.appabilities.sold.database.tables.CartItemModel;
import com.appabilities.sold.database.tables.CartItemModel_Table;
import com.appabilities.sold.model.OrderDetailModel;
import com.appabilities.sold.model.OrderModel;
import com.appabilities.sold.networking.APIError;
import com.appabilities.sold.networking.ErrorUtils;
import com.appabilities.sold.networking.MYPaymentError;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.screens.my_orders.view.MyOrderActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.craftman.cardform.Card;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;
import com.google.android.gms.common.api.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.oppwa.mobile.connect.exception.PaymentError;
import com.oppwa.mobile.connect.exception.PaymentException;
import com.oppwa.mobile.connect.payment.CheckoutInfo;
import com.oppwa.mobile.connect.payment.PaymentParams;
import com.oppwa.mobile.connect.payment.card.CardPaymentParams;
import com.oppwa.mobile.connect.provider.Connect;
import com.oppwa.mobile.connect.provider.ITransactionListener;
import com.oppwa.mobile.connect.provider.Transaction;
import com.oppwa.mobile.connect.service.ConnectService;
import com.oppwa.mobile.connect.service.IProviderBinder;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;

import static java.net.HttpURLConnection.HTTP_OK;


public class PaymentScreen extends AppCompatActivity implements ITransactionListener {

    CardForm cardForm;
    TextView txtDes, error_msg;
    ProgressBar progressBar;
    Button pay;
    private static final String APPLICATIONIDENTIFIER = "YOUR APP ID";
    private static final String PROFILETOKEN = "YOUR PROFILE TOKEN";
    public String checkoutId;
    String resourcePath = "";

    OrderModel orderModel;

    private IProviderBinder binder;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (IProviderBinder) service;
            binder.addTransactionListener(PaymentScreen.this);
        /* we have a connection to the service */
            try {
                binder.initializeProvider(Connect.ProviderMode.LIVE);

            } catch (PaymentException ee) {
        /* error occurred */
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_payment_screen);


        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //Check Os Ver For Set Status Bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }


        //setup items
        cardForm = (CardForm) findViewById(R.id.cardform);
        txtDes = (TextView) findViewById(R.id.payment_amount);
        pay = (Button) findViewById(R.id.btn_pay);
        progressBar = findViewById(R.id.progress_bar);
        error_msg = findViewById(R.id.error_msg);

        checkoutId = getIntent().getStringExtra("checkoutId");
        orderModel = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.ORDER_ITEM.name()));

        txtDes.setText(orderModel.getTotalAmount());
        pay.setText(String.format("Payer %s", txtDes.getText()));

        cardForm.setPayBtnClickListner(new OnPayBtnClickListner() {
            @Override
            public void onClick(final Card card) {

                try {

                    //Adapt month format when one char add 0 on left
                    String expMonth = "";
                    if (card.getExpMonth().toString().length() == 1) {
                        expMonth = "0" + card.getExpMonth();
                    } else {
                        expMonth = card.getExpMonth().toString();
                    }


                    Log.e("checkoutID ", checkoutId);

                    //collect card data
                    PaymentParams paymentParams = new CardPaymentParams(
                            checkoutId,
                            card.getBrand().toUpperCase(),
                            card.getNumber(),
                            card.getName().toUpperCase(),
                            expMonth,
                            card.getExpYear() + "",
                            card.getCVC()
                    );


                    //mack transaction
                    Transaction transaction = null;
                    transaction = new Transaction(paymentParams);

                    Log.e("INFOOO ", transaction.getPaymentParams().getCheckoutId() + " " + transaction.getPaymentParams().getPaymentBrand());

                    //binder.addTransactionListener(PaymentScreen.this);
                    cardForm.setVisibility(View.GONE);
                    error_msg.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    binder.submitTransaction(transaction);

                } catch (PaymentException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }


            }

        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ConnectService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
        stopService(new Intent(this, ConnectService.class));
    }


    @Override
    public void paymentConfigRequestSucceeded(CheckoutInfo checkoutInfo) {
        Log.e("params: ", "" + checkoutInfo.getResourcePath());
        //Toast.makeText(getApplication(),"config success"+" Checkout Id : "+ checkoutId, Toast.LENGTH_LONG).show();
        resourcePath = checkoutInfo.getResourcePath();
        getCheckoutInfo();
    }

    @Override
    public void paymentConfigRequestFailed(PaymentError paymentError) {
        Log.e("params: ", "" + paymentError.getErrorMessage());
        //Toast.makeText(getApplication(),"config error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void transactionCompleted(Transaction transaction) {
        try {
            binder.requestCheckoutInfo(checkoutId);
        } catch (PaymentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void transactionFailed(Transaction transaction, final PaymentError paymentError) {
        Log.e("Not sucsess", " Checkout Id : " + transaction.getPaymentParams().getCheckoutId() + paymentError.getErrorInfo());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                error_msg.setVisibility(View.VISIBLE);
                error_msg.setText(paymentError.getErrorInfo());
            }
        });
    }

    private void getCheckoutInfo(){
        /*Call<JsonElement> call = NetController.getInstance().getPaymentStatusService().getPaymentStatus(resourcePath);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("tag", "json element: " + response.body());
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().getAsString());
                        if (jsonObject.getJSONObject("result").getString("code").equalsIgnoreCase("000.000.000")) {
                            submitOrderToDatabase();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            error_msg.setVisibility(View.VISIBLE);
                            error_msg.setText(jsonObject.getJSONObject("result").getString("description"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (response.errorBody() != null) {
                    MYPaymentError error = ErrorUtils.parsePaymentError(response);
                    if(error.result.code.equalsIgnoreCase("000.000.000")){
                        submitOrderToDatabase();
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        error_msg.setVisibility(View.VISIBLE);
                        //error_msg.setText(error.result.description);
                        error_msg.setText("Payment Declined");
                    }

                    Log.d("ZEE", "onResponse: "+error);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                error_msg.setVisibility(View.VISIBLE);
                error_msg.setText("Payment was declined due to some reasons");
                SnackUtils.showSnackMessage(PaymentScreen.this, "Error in submitting order");
            }
        });*/
    }

    private void submitOrderToDatabase() {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(orderModel);
        Call<BaseResponse> call2 = NetController.getInstance().getOrderService().submitOrder(json);

        call2.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {
                if (response.code() == HTTP_OK) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                    SnackUtils.showSnackMessage(PaymentScreen.this, "Order Submitted Successfully");
                    for (OrderDetailModel item :
                            orderModel.getOrderDetails()) {
                        deleteShoppingCartItem(item.getProductID());
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(PaymentScreen.this, MyOrderActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(AppConstants.KEYS.IS_REFRESH.name(), true);
                            startActivity(intent);
                            finish();
                        }
                    }, 800);
                } else {
                    SnackUtils.showSnackMessage(PaymentScreen.this, "Error in submitting order");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                SnackUtils.showSnackMessage(PaymentScreen.this, "Error in submitting order");

            }
        });
//        call2.enqueue(new Callback<BaseResponse>() {
//            @Override
//            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
//                if (response.code() == HTTP_OK) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    });
//
//                    SnackUtils.showSnackMessage(PaymentScreen.this, "Order Submitted Successfully");
//                    for (OrderDetailModel item :
//                            orderModel.getOrderDetails()) {
//                        deleteShoppingCartItem(item.getProductID());
//                    }
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            Intent intent = new Intent(PaymentScreen.this, MyOrderActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.putExtra(AppConstants.KEYS.IS_REFRESH.name(), true);
//                            startActivity(intent);
//                            finish();
//                        }
//                    }, 800);
//                } else {
//                    SnackUtils.showSnackMessage(PaymentScreen.this, "Error in submitting order");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponse> call, Throwable t) {
//                SnackUtils.showSnackMessage(PaymentScreen.this, "Error in submitting order");
//            }
//        });
    }

    public void deleteShoppingCartItem(String productID) {
        SQLite.delete(CartItemModel.class)
                .where(CartItemModel_Table.productId.is(productID))
                .async()
                .execute();
    }
}

