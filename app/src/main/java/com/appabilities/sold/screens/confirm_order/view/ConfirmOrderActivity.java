package com.appabilities.sold.screens.confirm_order.view;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.database.tables.CartItemModel;
import com.appabilities.sold.database.tables.CartItemModel_Table;
import com.appabilities.sold.databinding.ActivityConfirmOrderBinding;
import com.appabilities.sold.databinding.ConfirmOrderItemLayoutBinding;
import com.appabilities.sold.model.OrderDetailModel;
import com.appabilities.sold.model.OrderModel;
import com.appabilities.sold.networking.ErrorUtils;
import com.appabilities.sold.networking.MYPaymentError;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.PaymentResponse;
import com.appabilities.sold.networking.response.VATResponse;
import com.appabilities.sold.screens.confirm_order.ConfirmOrderContract;
import com.appabilities.sold.screens.confirm_order.ConfirmOrderPresenter;
import com.appabilities.sold.screens.my_orders.view.MyOrderActivity;
import com.appabilities.sold.screens.payment_webview.OnlinePaymentActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.google.gson.JsonElement;
import com.oppwa.mobile.connect.checkout.dialog.CheckoutActivity;
import com.oppwa.mobile.connect.checkout.meta.CheckoutSettings;
import com.oppwa.mobile.connect.exception.PaymentError;
import com.oppwa.mobile.connect.exception.PaymentException;
import com.oppwa.mobile.connect.provider.Connect;
import com.oppwa.mobile.connect.provider.Transaction;
import com.oppwa.mobile.connect.provider.TransactionType;
import com.oppwa.mobile.connect.service.ConnectService;
import com.oppwa.mobile.connect.service.IProviderBinder;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function4;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmOrderActivity extends BaseActivity implements ConfirmOrderContract.View,
        Function4<View, OrderDetailModel, Integer, Map<Integer, ? extends View>, Unit>,
        RadioGroup.OnCheckedChangeListener {

    ActivityConfirmOrderBinding bi;
    ConfirmOrderPresenter presenter;
    RecyclerAdapterUtil<OrderDetailModel> mOrderAdapter;
    OrderModel orderModel;
    int paymentMethodStatus = -1;
    private IProviderBinder binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(ConfirmOrderActivity.this, R.layout.activity_confirm_order);
        bi.setView(this);
        presenter = new ConfirmOrderPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CONFIRM ORDER");

        presenter.callGetSetting();
    }

    @Override
    public void onGetSettingResponse(VATResponse vatResponse) {
        orderModel = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.ORDER_ITEM.name()));
        bi.txtSubTotalConfirm.setText(""+orderModel.getTotalAmount());
        float vatamount = Float.valueOf(vatResponse.vat) * Float.valueOf(orderModel.getTotalAmount()) / 100;
        orderModel.getOrderDetails().get(0).setTaxes(String.valueOf(vatamount));
        bi.taxesAmount.setText(orderModel.getOrderDetails().get(0).getTaxes());
        orderModel.setTotalAmount(String.valueOf(Float.valueOf(orderModel.getTotalAmount()) + vatamount));

        updateUI();
    }

    @Override
    public void onFailed(String error) {
        SnackUtils.showErrorMessage(this, error);
    }

    @Override
    public void updateUI() {
        mOrderAdapter = new RecyclerAdapterUtil<>(this, orderModel.getOrderDetails(), R.layout.confirm_order_item_layout);
        mOrderAdapter.addOnDataBindListener(this);
        bi.recyclerItemsConfirm.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        bi.recyclerItemsConfirm.setLayoutManager(layoutManager);
        bi.recyclerItemsConfirm.setAdapter(mOrderAdapter);
        bi.radioGroupPayment.setOnCheckedChangeListener(this);


        bi.txtTotalConfirm.setText("SAR "+orderModel.getTotalAmount());
        bi.txtNameConfirm.setText(loginResponse.display_name);
        bi.txtEmailConfirm.setText(loginResponse.email);
        bi.txtAddressConfirm.setText(loginResponse.address);
        bi.txtPhoneConfirm.setText(loginResponse.phone);

    }

    @Override
    public void onClickPlaceOrder() {
        bi.btnPlaceOrder.startAnimation();
        orderModel.setAccessToken(loginResponse.access_token);
        orderModel.setEmail(bi.txtEmailConfirm.getText().toString().trim());
        orderModel.setFullname(bi.txtNameConfirm.getText().toString().trim());
        orderModel.setShipping_address(bi.txtAddressConfirm.getText().toString().trim());
        orderModel.setPhone(bi.txtPhoneConfirm.getText().toString().trim());
        if (paymentMethodStatus == -1){
            SnackUtils.showSnackMessage(ConfirmOrderActivity.this, "Select Payment Method To Continue");
            bi.btnPlaceOrder.revertAnimation();
            return;
        }else if (paymentMethodStatus == 1){
            orderModel.setPayment_method("COD");
        }else if (paymentMethodStatus == 2){
            orderModel.setPayment_method("ONLINE");
            String merchantUserID = loginResponse.userID + "" +new Date().getTime();
            orderModel.setMerchantTransactionId(merchantUserID);
        }
        presenter.placeOrder(orderModel);
    }

    @Override
    public void successfullySubmitOrder() {
        bi.btnPlaceOrder.revertAnimation();
        bi.btnPlaceOrder.setVisibility(View.GONE);
        SnackUtils.showSnackMessage(this, "Order Submitted Successfully");
        for (OrderDetailModel item: orderModel.getOrderDetails()) {
            deleteShoppingCartItem(item.getProductID());
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =new Intent(ConfirmOrderActivity.this, MyOrderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(AppConstants.KEYS.IS_REFRESH.name(), true);
                startActivity(intent);
                finish();
            }
        }, 700);
    }

    @Override
    public void errorSubmitOrder() {
        SnackUtils.showSnackMessage(this, "Error in submitting order");
    }

    @Override
    public void showNameError(String s) {
        bi.btnPlaceOrder.revertAnimation();
        bi.txtNameConfirm.setError(s);
    }

    @Override
    public void showEmailError(String s) {
        bi.btnPlaceOrder.revertAnimation();
        bi.txtEmailConfirm.setError(s);
    }

    @Override
    public void showPhoneError(String s) {
        bi.btnPlaceOrder.revertAnimation();
        bi.txtPhoneConfirm.setError(s);
    }

    @Override
    public void showAddressError(String s) {
        bi.btnPlaceOrder.revertAnimation();
        bi.txtAddressConfirm.setError(s);
    }

    @Override
    public void deleteShoppingCartItem(String productID) {
        SQLite.delete(CartItemModel.class)
                .where(CartItemModel_Table.productId.is(productID))
                .async()
                .execute();
    }

    @Override
    public void updateCheckoutID(final PaymentResponse body) {
        bi.btnPlaceOrder.revertAnimation();
        Log.d("tag", "checkoutid: "+body.id);
        Intent intent = new Intent(this, OnlinePaymentActivity.class);
        intent.putExtra("checkoutId", body.id);
        intent.putExtra(AppConstants.KEYS.ORDER_ITEM.name(), Parcels.wrap(orderModel));
        startActivity(intent);

        /*Intent intent = new Intent(this, PaymentScreen.class);
        intent.putExtra("checkoutId", body.id);
        intent.putExtra(AppConstants.KEYS.ORDER_ITEM.name(), Parcels.wrap(orderModel));*/
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(intent);
        //finish();

        //checkoutSettings.setShopperResultUrl("companyname://result");
        //CheckoutSettings checkoutSettings = new CheckoutSettings(checkoutId, paymentBrands, Connect.ProviderMode.TEST);

        //ComponentName componentName = new ComponentName("com.appabilities.hyperpayintegration", "CheckoutBroadcastReceiver");


        /*Intent intent = checkoutSettings.createCheckoutActivityIntent(this);
        startActivityForResult(intent, CheckoutActivity.REQUEST_CODE_CHECKOUT);*/

        /*Set<String> paymentBrands = new LinkedHashSet<String>();
        paymentBrands.add("VISA");
        CheckoutSettings checkoutSettings = new CheckoutSettings(body.id, paymentBrands);
        checkoutSettings.setWebViewEnabledFor3DSecure(true);

        Intent intent = new Intent(ConfirmOrderActivity.this, CheckoutActivity.class);
        intent.putExtra(CheckoutActivity.CHECKOUT_SETTINGS, checkoutSettings);
        startActivityForResult(intent, CheckoutActivity.CHECKOUT_ACTIVITY);*/
    }

    @Override
    public void errorInGettingCheckoutID() {
        bi.btnPlaceOrder.revertAnimation();
        SnackUtils.showSnackMessage(ConfirmOrderActivity.this, "Error In Getting CheckoutID");
    }

    @Override
    public Unit invoke(View view, OrderDetailModel orderDetailModel, Integer integer, Map<Integer, ? extends View> integerMap) {
        ConfirmOrderItemLayoutBinding binding = DataBindingUtil.bind(view);
        binding.setOrderModel(orderDetailModel);
        binding.executePendingBindings();
        binding.txtNameFooditem.setText(orderDetailModel.getQuantity() +" x " + orderDetailModel.getProductTitle());
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId){
            case R.id.radio_btn_cod:
                paymentMethodStatus = 1;
                break;
            case R.id.radio_btn_credit:
                paymentMethodStatus = 2;
                break;
        }
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

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (IProviderBinder) service;

            try {
                binder.initializeProvider(Connect.ProviderMode.TEST);
            } catch (PaymentException ee) {

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case CheckoutActivity.RESULT_OK:
            /* transaction completed */

                Log.d("tag", "CheckoutActivity.RESULT_OK");
                Transaction transaction = data.getParcelableExtra(CheckoutActivity.CHECKOUT_RESULT_TRANSACTION);
                bi.btnPlaceOrder.startAnimation();
                //presenter.placeOrder(orderModel);

                /*try {
                    binder.submitTransaction(transaction);
                } catch (PaymentException e) {
                    Log.d("submitTransaction e", e.getMessage());
                }*/

            /* resource path if needed */
                String resourcePath = data.getStringExtra(CheckoutActivity.CHECKOUT_RESULT_RESOURCE_PATH);

                if (transaction.getTransactionType() == TransactionType.SYNC) {
                /* check the paymentErrorResult of synchronous transaction */
                } else {
                /* wait for the asynchronous transaction callback in the onNewIntent() */
                }

                break;
            case CheckoutActivity.RESULT_CANCELED:
            /* shopper canceled the checkout process */
                /*CHECKOUT_PAYMENT_BUTTON_METHOD
                CHECKOUT_RESULT_SETTINGS
                CHECKOUT_RESULT_TRANSACTION
                CHECKOUT_RESULT_RESOURCE_PATH
                CHECKOUT_RESULT_ERROR
                ACTION_PAYMENT_METHOD_SELECTED*/
                String resourcepath = data.getStringExtra(CheckoutActivity.CHECKOUT_RESULT_RESOURCE_PATH);
                getCheckoutInfo(resourcepath, AppConstants.PAYMENT_KEYS_TEST.USER_ID, AppConstants.PAYMENT_KEYS_TEST.PASSWORD, AppConstants.PAYMENT_KEYS_TEST.ENTITY_ID);
                Log.d("tag", "resourcepath: "+resourcepath);
                break;
            case CheckoutActivity.RESULT_ERROR:
            /* error occurred */
                PaymentError error = data.getParcelableExtra(CheckoutActivity.CHECKOUT_RESULT_ERROR);
                Log.d("tag", "CheckoutActivity.RESULT_ERROR");
        }
    }


    private void getCheckoutInfo(String resourcePath, String userId, String password, String entityId){
        Call<JsonElement> call = NetController.getInstance().getPaymentStatusService().getPaymentStatus(resourcePath, userId, password, entityId);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("tag", "json element: " + response.body());
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().getAsString());
                        if (jsonObject.getJSONObject("result").getString("code").equalsIgnoreCase("000.000.000")) {

                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (response.errorBody() != null) {
                    MYPaymentError error = ErrorUtils.parsePaymentError(response);
                    if(error.result.code.equalsIgnoreCase("000.000.000")){

                    }
                    else {

                    }

                    Log.d("ZEE", "onResponse: "+error);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                SnackUtils.showSnackMessage(ConfirmOrderActivity.this, "Error in submitting order");
            }
        });
    }

}
