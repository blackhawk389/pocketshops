package com.appabilities.sold.screens.my_order_detail.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivityMyOrderDetailBinding;
import com.appabilities.sold.databinding.OrderDetailItemLayoutBinding;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.OrderResponse;
import com.appabilities.sold.networking.response.OrderResponseDetail;
import com.appabilities.sold.networking.response.VATResponse;
import com.appabilities.sold.screens.my_order_detail.MyOrderDetailContract;
import com.appabilities.sold.screens.my_order_detail.MyOrderDetailPresenter;
import com.appabilities.sold.screens.product_review.view.ReviewDialogFragment;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;

import org.parceler.Parcels;

import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function4;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

public class MyOrderDetailActivity extends BaseActivity implements MyOrderDetailContract.View, Function4<View, OrderResponseDetail, Integer, Map<Integer, ? extends View>, Unit> {

    ActivityMyOrderDetailBinding bi;
    MyOrderDetailPresenter presenter;
    OrderResponse orderResponse;
    RecyclerAdapterUtil<OrderResponseDetail> mOrderDetailAdapter;
    OrderDetailItemLayoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_my_order_detail);
       // bi = DataBindingUtil.setContentView(this, R.layout.activity_my_order_detail);
        bi.setView(this);
        presenter = new MyOrderDetailPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ORDER DETAILS");

        orderResponse = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.ORDER_ITEM.name()));
        bi.setOrderItem(orderResponse);

        bi.taxesAmount.setText(orderResponse.orderDetail.get(0).taxes);
        bi.txtSubTotalConfirm.setText(""+(Float.valueOf(orderResponse.totalAmt) - Float.valueOf(orderResponse.orderDetail.get(0).taxes)));
        mOrderDetailAdapter = new RecyclerAdapterUtil<>(this, orderResponse.orderDetail, R.layout.order_detail_item_layout);
        mOrderDetailAdapter.addOnDataBindListener(this);

        bi.recyclerItemsConfirm.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        bi.recyclerItemsConfirm.setLayoutManager(layoutManager);
        bi.recyclerItemsConfirm.setAdapter(mOrderDetailAdapter);
        bi.txtPhone.setText(orderResponse.phone);



    }

    @Override
    public Unit invoke(View view, final OrderResponseDetail orderResponseDetail, Integer integer, Map<Integer, ? extends View> integerMap) {
         binding = DataBindingUtil.bind(view);
        binding.setOrderDetailModel(orderResponseDetail);
        binding.executePendingBindings();
        binding.txtNameFooditem.setText(orderResponseDetail.qty+" x "+orderResponseDetail.productTitle);
        if (orderResponseDetail.orderStatus.equals("In-Transit")){
            binding.btnPlaceOrderConfirm.setVisibility(View.VISIBLE);
        }else if (orderResponseDetail.orderStatus.equals("Received")){
            if (orderResponseDetail.isReviewed.equals("0")){
                binding.btnPlaceOrderConfirm.setVisibility(View.VISIBLE);
                binding.btnPlaceOrderConfirm.setText("Submit Review");
            }else{
                binding.btnPlaceOrderConfirm.setVisibility(View.GONE);
            }
        }
        binding.btnPlaceOrderConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderResponseDetail.orderStatus.equals("Received")
                        && (orderResponseDetail!= null
                        && orderResponseDetail.isReviewed.equalsIgnoreCase("0"))
                        || binding.btnPlaceOrderConfirm.getText().equals("Submit Review")){
                    ReviewDialogFragment.newInstance(orderResponseDetail, loginResponse.access_token, MyOrderDetailActivity.this).show(getSupportFragmentManager(), "Submit Review");
                }else{

                    AlertDialog dialog = new AlertDialog.Builder(MyOrderDetailActivity.this)
                            .setTitle("Product Recieved")
                            .setMessage("Have you received the product?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Call<BaseResponse> callUpdateStatus = NetController.getInstance().getOrderService().updateOrderStatus(loginResponse.access_token,
                                            orderResponseDetail.orderDID, "Received");
                                    callUpdateStatus.enqueue(new Callback<BaseResponse>() {
                                        @Override
                                        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                                            if (response.code() == HTTP_OK) {
                                                SnackUtils.showSnackMessage(MyOrderDetailActivity.this, "Status Updated Successfully");
                                                binding.progressConfirm.setVisibility(View.GONE);
                                                binding.btnPlaceOrderConfirm.setText("Submit Review");
                                                binding.txtStatus.setText("Received");
                                                binding.btnPlaceOrderConfirm.setVisibility(View.VISIBLE);
                                                isChanged = true;
                                            } else {
                                                SnackUtils.showSnackMessage(MyOrderDetailActivity.this, "Error In Updating Status");
                                                binding.progressConfirm.setVisibility(View.GONE);
                                                binding.btnPlaceOrderConfirm.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<BaseResponse> call, Throwable t) {
                                            binding.progressConfirm.setVisibility(View.GONE);
                                            binding.btnPlaceOrderConfirm.setVisibility(View.VISIBLE);
                                            SnackUtils.showSnackMessage(MyOrderDetailActivity.this, "Error In Updating Status");
                                        }
                                    });

                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    binding.progressConfirm.setVisibility(View.GONE);
                                    binding.btnPlaceOrderConfirm.setVisibility(View.VISIBLE);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
//                    binding.progressConfirm.setVisibility(View.VISIBLE);
//                    binding.btnPlaceOrderConfirm.setVisibility(View.GONE);

                }
            }
        });
        return null;
    }

    public void hideButton() {
        binding.btnPlaceOrderConfirm.setVisibility(View.GONE);
        isChanged = true;
    }
}
