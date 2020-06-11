package com.appabilities.sold.screens.my_sale_detail.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivitySaleDetailBinding;
import com.appabilities.sold.databinding.SaleDetailItemLayoutBinding;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.SaleResponse;
import com.appabilities.sold.screens.my_sale_detail.SaleDetailContract;
import com.appabilities.sold.screens.my_sale_detail.SaleDetailPresenter;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function4;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

public class SaleDetailActivity extends BaseActivity implements SaleDetailContract.View, Function4<View, SaleResponse, Integer, Map<Integer, ? extends View>, Unit> {

    ActivitySaleDetailBinding bi;
    SaleDetailPresenter presenter;
    SaleResponse saleResponse;
    RecyclerAdapterUtil<SaleResponse> mAdapter;
    List<SaleResponse> saleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_sale_detail);
        presenter = new SaleDetailPresenter(this);
        bi.setView(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("SALES DETAIL");

        saleResponse = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.SALE_ITEM.name()));
        bi.setSaleItem(saleResponse);

        bi.taxesAmount.setText(saleResponse.taxes);
        bi.txtSubTotalConfirm.setText(""+(Float.valueOf(saleResponse.totalPrice)));
        bi.txtTotalConfirm.setText("SAR "+""+(Float.valueOf(saleResponse.totalPrice) + Float.valueOf(saleResponse.taxes)));

        saleList = new ArrayList<>();
        saleList.add(saleResponse);
        mAdapter = new RecyclerAdapterUtil<>(this, saleList, R.layout.sale_detail_item_layout);
        mAdapter.addOnDataBindListener(this);

        bi.recyclerItemsConfirm.setHasFixedSize(true);
        bi.recyclerItemsConfirm.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        bi.recyclerItemsConfirm.setAdapter(mAdapter);
    }

    @Override
    public Unit invoke(View view, final SaleResponse saleResponse, final Integer integer, Map<Integer, ? extends View> integerMap) {
        final SaleDetailItemLayoutBinding binding = DataBindingUtil.bind(view);
        binding.setSaleDetailModel(saleResponse);
        binding.executePendingBindings();

        binding.txtNameFooditem.setText(saleResponse.qty + " x " +saleResponse.productTitle);
        if (saleResponse.orderStatus.equals("In-Process")){
            binding.btnPlaceOrderConfirm.setVisibility(View.VISIBLE);
            binding.btnPlaceOrderConfirm.setText("In-Transit");
        }

        binding.btnPlaceOrderConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog = new AlertDialog.Builder(SaleDetailActivity.this)
                        .setTitle("In Transit")
                        .setMessage("Is this item now in transit?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                binding.progressConfirm.setVisibility(View.VISIBLE);
                                binding.btnPlaceOrderConfirm.setVisibility(View.VISIBLE);

                                Call<BaseResponse> callUpdateStatus =
                                        NetController.getInstance().getOrderService().updateOrderStatus(loginResponse.access_token, saleResponse.orderDID, "In-Transit");
                                callUpdateStatus.enqueue(new Callback<BaseResponse>() {
                                    @Override
                                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                                        if (response.code() == HTTP_OK){
                                            if (saleResponse.orderStatus.equals("In-Process")){
                                                binding.txtStatus.setText("In-Transit");
                                                mAdapter.getItemList().get(integer).orderStatus = "In-Transit";
                                                // binding.btnPlaceOrderConfirm.setText("In-Transit");
                                                binding.btnPlaceOrderConfirm.setVisibility(View.GONE);
                                                mAdapter.notifyDataSetChanged();
                                                setChanged(true);
                                            }
                                            SnackUtils.showSnackMessage(SaleDetailActivity.this, "Status Updated Successfully");
                                            binding.progressConfirm.setVisibility(View.GONE);
                                        }else {
                                            binding.btnPlaceOrderConfirm.setVisibility(View.VISIBLE);
                                            binding.progressConfirm.setVisibility(View.GONE);
                                            SnackUtils.showSnackMessage(SaleDetailActivity.this, "Error In Updating Status");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                                        SnackUtils.showSnackMessage(SaleDetailActivity.this, "Error In Updating Status");
                                        binding.btnPlaceOrderConfirm.setVisibility(View.VISIBLE);
                                        binding.progressConfirm.setVisibility(View.GONE);
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });
        return null;
    }
}
