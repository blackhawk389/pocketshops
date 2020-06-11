package com.appabilities.sold.screens.my_products.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.sidemenu.SideMenuBaseActivity;
import com.appabilities.sold.databinding.ActivityMyProductBinding;
import com.appabilities.sold.databinding.ProductItemLayoutBinding;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.screens.add_product.view.AddProductActivity;
import com.appabilities.sold.screens.my_product_detail.view.MyProductDetailActivity;
import com.appabilities.sold.screens.my_products.MyProductsContract;
import com.appabilities.sold.screens.my_products.MyProductsPresenter;
import com.appabilities.sold.screens.product_detail.view.ProductDetailActivity;
import com.appabilities.sold.utils.AppConstants;
import com.facebook.drawee.view.SimpleDraweeView;
import com.iamhabib.easy_preference.EasyPreference;
import com.kennyc.view.MultiStateView;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;

public class MyProductsActivity extends SideMenuBaseActivity implements
        MyProductsContract.View<List<ProductResponse>>, Function2<ProductResponse, Integer, Unit> {

    ActivityMyProductBinding bi;
    MyProductsPresenter presenter;
    List<ProductResponse> myProductList;
    RecyclerAdapterUtil myProductAdapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = (ActivityMyProductBinding) setContentLayout(R.layout.activity_my_product);
        bi.setView(this);
        presenter = new MyProductsPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        initDrawer(bi.toolbar, "My Products");

        myProductList = new ArrayList<>();
        myProductAdapter = new RecyclerAdapterUtil<>(this, myProductList, R.layout.product_item_layout);
        myProductAdapter.addOnDataBindListener(new Function4<View, ProductResponse, Integer, Map<Integer, ? extends View>, Unit>() {
            @Override
            public Unit invoke(View view, ProductResponse productResponse, Integer pos, Map<Integer, ? extends View> integerMap) {
                ProductItemLayoutBinding binding = DataBindingUtil.bind(view);
                binding.setProductModel(productResponse);
                binding.executePendingBindings();

                binding.btnFavouriteProductitem.setVisibility(View.GONE);

                binding.ratingProductProductitem.setRating(Float.valueOf(productResponse.ratings));

                if (productResponse.auctionable.equals("1")){
                    binding.slantedAuctionableProductitem.setVisibility(View.VISIBLE);
                }

                if (productResponse.sponsered.equals("1"))
                    binding.txtStatusSponser.setVisibility(View.VISIBLE);

                return null;
            }
        });
        myProductAdapter.addOnClickListener(this);
        bi.recyclerMyProducts.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        bi.recyclerMyProducts.setLayoutManager(layoutManager);
        bi.recyclerMyProducts.setAdapter(myProductAdapter);
        presenter.getMyProductList(loginResponse.access_token);
    }

    @Override
    public void onLoading() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void onRequestSuccessful(List<ProductResponse> responseData) {
        myProductAdapter.getItemList().clear();
        myProductAdapter.getItemList().addAll(responseData);
        myProductAdapter.notifyDataSetChanged();
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
    }

    @Override
    public void onRequestFail(String errorMessage) {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void onRequestNotFound() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
    }

    @Override
    public void onClickAddProduct() {
        startActivity(new Intent(MyProductsActivity.this, AddProductActivity.class));
    }

    @Override
    public Unit invoke(ProductResponse productResponse, Integer position) {
        View view = layoutManager.findViewByPosition(position);
        SimpleDraweeView imgProduct = (SimpleDraweeView) view.findViewById(R.id.imgProduct_productitem);
        Intent intent = new Intent(this, MyProductDetailActivity.class);
        intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
        startActivity(intent);
        /*int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            intent.putExtra(AppConstants.KEYS.TRANSITION_VIEW.name(), ViewCompat.getTransitionName(imgProduct));
            intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    imgProduct,
                    ViewCompat.getTransitionName(imgProduct));

            startActivity(intent, options.toBundle());
        }else {
            intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
            startActivity(intent);
        }*/

        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        selectMenuOption(R.id.nav_my_products);
        if (EasyPreference.with(MyProductsActivity.this).getBoolean(AppConstants.KEYS.IS_REFRESH.name(), false)){
            presenter.getMyProductList(loginResponse.access_token);
            EasyPreference.with(MyProductsActivity.this).remove(AppConstants.KEYS.IS_REFRESH.name()).save();
        }
    }
}
