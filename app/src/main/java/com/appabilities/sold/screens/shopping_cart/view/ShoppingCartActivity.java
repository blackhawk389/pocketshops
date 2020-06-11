package com.appabilities.sold.screens.shopping_cart.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.database.tables.CartItemModel;
import com.appabilities.sold.databinding.ActivityShoppingCartBinding;
import com.appabilities.sold.databinding.CartItemLayoutBinding;
import com.appabilities.sold.model.OrderDetailModel;
import com.appabilities.sold.model.OrderModel;
import com.appabilities.sold.screens.confirm_order.view.ConfirmOrderActivity;
import com.appabilities.sold.screens.shopping_cart.ShoppingCartContract;
import com.appabilities.sold.screens.shopping_cart.ShoppingCartPresenter;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.kennyc.view.MultiStateView;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function4;
import me.himanshusoni.quantityview.QuantityView;

public class ShoppingCartActivity extends BaseActivity implements QuantityView.OnQuantityChangeListener,ShoppingCartContract.View<List<CartItemModel>>, Function4<View, CartItemModel, Integer, Map<Integer, ? extends View>, Unit>, CompoundButton.OnCheckedChangeListener {

    ActivityShoppingCartBinding bi;
    ShoppingCartPresenter presenter;
    RecyclerAdapterUtil<CartItemModel> mCartAdapter;
    List<CartItemModel> listCart;
    private List<CartItemModel> cartResponseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_shopping_cart);
        bi.setView(this);
        presenter = new ShoppingCartPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("SHOPPING CART");
        listCart = new ArrayList<>();
        bi.recyclerItemsShopcart.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        bi.recyclerItemsShopcart.setLayoutManager(layoutManager);
        mCartAdapter = new RecyclerAdapterUtil<>(this, listCart, R.layout.cart_item_layout);
        mCartAdapter.addOnDataBindListener(this);
        bi.recyclerItemsShopcart.setAdapter(mCartAdapter);
        presenter.getCartItems();

        bi.checkSelectAllItemsShopcart.setOnCheckedChangeListener(this);
    }

    @Override
    public void onLoading() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void onRequestSuccessful(List<CartItemModel> responseData) {
        if (responseData != null && responseData.size() > 0) {
            mCartAdapter.getItemList().addAll(responseData);
            mCartAdapter.notifyDataSetChanged();
            cartResponseData = responseData;
            bi.txtTotalItemsShopcart.setText("(" + responseData.size() + ")");
            refreshShoppingCart();
            bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        } else {
            bi.txtTotalAmountShopcart.setText("SAR " + 0.00);
            bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        }

    }

    @Override
    public void onRequestFail(String errorMessage) {

    }

    @Override
    public void onRequestNotFound() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
    }

    @Override
    public void refreshShoppingCart() {
        float totalPrice = 0f;
        int includedCount = 0;
        for (CartItemModel cartitem : mCartAdapter.getItemList()) {


            float itemAmount = (cartitem.quantity) * Float.parseFloat(cartitem.price);
            if ((cartitem.isIncluded)) {
                includedCount++;
            } else {
                itemAmount = 0;
            }
            totalPrice += itemAmount;
        }

        bi.txtTotalAmountShopcart.setText("SAR " + totalPrice);
        bi.btnBuyShopcart.setText("Checkout (" + includedCount + ")");
        bi.txtTotalItemsShopcart.setText("(" + mCartAdapter.getItemList().size() + ")");
    }

    @Override
    public void refreshShoppingCart(boolean state) {
        float totalPrice = 0f;
        int includedCount = 0;
        for (CartItemModel cartitem : mCartAdapter.getItemList()) {
            float itemAmount = (cartitem.quantity) * Float.parseFloat(cartitem.price);
            cartitem.isIncluded = state;
            cartitem.save();

            if (cartitem.isIncluded)
                totalPrice += itemAmount;
        }

        mCartAdapter.notifyDataSetChanged();

        bi.txtTotalAmountShopcart.setText("SAR " + totalPrice);
        bi.btnBuyShopcart.setText("Buy (" + includedCount + ")");
    }

    @Override
    public void onClickCheckOut() {
        List<OrderDetailModel> orderDetailList = new ArrayList<>();
        for (CartItemModel cartItemModel : mCartAdapter.getItemList()) {
            if (cartItemModel.isIncluded) {
                OrderDetailModel orderDetailModel = new OrderDetailModel();
                orderDetailModel.setProductTitle(cartItemModel.title);
                orderDetailModel.setProductID(cartItemModel.productId);
                orderDetailModel.setQuantity(String.valueOf(cartItemModel.quantity));
                orderDetailModel.setTotalItemPrice(String.valueOf(
                        cartItemModel.quantity * Double.valueOf(cartItemModel.price)));
                orderDetailModel.setItemPrice(cartItemModel.price);
                orderDetailModel.setItemURL(cartItemModel.imgUrl);
                orderDetailModel.setTaxes("0");
                orderDetailModel.setShipping("0");
                orderDetailList.add(orderDetailModel);
            }
        }

        if (orderDetailList.size() == 0) {
            SnackUtils.showSnackMessage(ShoppingCartActivity.this, "Cart Is Empty");
            return;
        }

        OrderModel orderModel = new OrderModel(
                bi.txtTotalAmountShopcart.getText().toString().substring(3).trim(),
                orderDetailList);

        Intent intent = new Intent(ShoppingCartActivity.this, ConfirmOrderActivity.class);
        intent.putExtra(AppConstants.KEYS.ORDER_ITEM.name(), Parcels.wrap(orderModel));
        startActivity(intent);
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
    public Unit invoke(View view, final CartItemModel cartItemModel, final Integer integer, Map<Integer, ? extends View> integerMap) {
        CartItemLayoutBinding binding = DataBindingUtil.bind(view);
        binding.setCartItem(cartItemModel);
        if (cartItemModel.isIncluded) {
            binding.checkSelectCartitem.setChecked(true);
        }else {
            binding.checkSelectCartitem.setChecked(false);
        }
        binding.quantityUnitCartitem.setMaxQuantity(cartItemModel.maxQuantity);
        binding.quantityUnitCartitem.setOnQuantityChangeListener(this);
        binding.quantityUnitCartitem.setQuantity(cartResponseData.get(integer).quantity);
        binding.imgRemoveCartitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartItemModel.delete();
                mCartAdapter.getItemList().remove((int) integer);
                mCartAdapter.notifyDataSetChanged();
                refreshShoppingCart();
            }
        });

        binding.checkSelectCartitem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CartItemModel cartItemModel = mCartAdapter.getItemList().get((int) integer);
                cartItemModel.isIncluded = isChecked;
                cartItemModel.save();

                refreshShoppingCart();

            }
        });

        binding.quantityUnitCartitem.setOnQuantityChangeListener(new QuantityView.OnQuantityChangeListener() {
            @Override
            public void onQuantityChanged(int oldQuantity, int newQuantity, boolean programmatically) {
                CartItemModel cartItemModel = mCartAdapter.getItemList().get((int) integer);
                cartItemModel.quantity = newQuantity;
                cartItemModel.save();

                refreshShoppingCart();

            }

            @Override
            public void onLimitReached() {

            }
        });

        return null;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        refreshShoppingCart(isChecked);
    }

    @Override
    public void onQuantityChanged(int oldQuantity, int newQuantity, boolean programmatically) {
//                if(newQuantity == cartItemModel.maxQuantity){
                    Toast.makeText(ShoppingCartActivity.this, "You have reached this product's maximum available quantity", Toast.LENGTH_SHORT).show();
//                }


    }

    @Override
    public void onLimitReached() {
        Toast.makeText(ShoppingCartActivity.this, "You have reached this product's maximum available quantity", Toast.LENGTH_SHORT).show();

    }
}
