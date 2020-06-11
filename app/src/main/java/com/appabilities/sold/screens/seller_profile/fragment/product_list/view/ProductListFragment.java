package com.appabilities.sold.screens.seller_profile.fragment.product_list.view;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseFragment;
import com.appabilities.sold.databinding.FragmentProductListBinding;
import com.appabilities.sold.databinding.ProductItemLayoutBinding;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.networking.response.SellerProfileResponse;
import com.appabilities.sold.networking.response.VerifyUserResponse;
import com.appabilities.sold.screens.product_detail.view.ProductDetailActivity;
import com.appabilities.sold.screens.seller_profile.fragment.product_list.ProductListContract;
import com.appabilities.sold.screens.seller_profile.fragment.product_list.ProductListPresenter;
import com.appabilities.sold.screens.seller_profile.view.SellerProfileActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kennyc.view.MultiStateView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductListFragment extends BaseFragment implements ProductListContract.View,
        Function4<View, ProductResponse, Integer, Map<Integer, ? extends View>, Unit>, Function2<ProductResponse, Integer, Unit> {

    RecyclerAdapterUtil<ProductResponse> mProductAdapter;
    FragmentProductListBinding bi;
    LoginResponse loginResponse;
    LinearLayoutManager layoutManager;
    ProductListPresenter presenter;
    private SellerProfileResponse mSellerProfile;
    private static final String ARG_PARAM1 = "param1";

    public static ProductListFragment newInstance(SellerProfileResponse mSellerProfile){
        ProductListFragment mFragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, Parcels.wrap(mSellerProfile));
        mFragment.setArguments(args);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mSellerProfile = Parcels.unwrap(getArguments().getParcelable(ARG_PARAM1));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bi = DataBindingUtil.inflate(inflater, R.layout.fragment_product_list, container, false);
        bi.setView(this);
        presenter = new ProductListPresenter(this);
        presenter.initScreen();
        bi.setPresenter(presenter);
        return bi.getRoot();
    }

    @Override
    public Unit invoke(View view, final ProductResponse productResponse, Integer integer, Map<Integer, ? extends View> integerMap) {
        ProductItemLayoutBinding binding = DataBindingUtil.bind(view);
        binding.setProductModel(productResponse);
        binding.executePendingBindings();

        if (productResponse.auctionable.equals("1"))
            binding.slantedAuctionableProductitem.setVisibility(View.VISIBLE);
        else
            binding.slantedAuctionableProductitem.setVisibility(View.GONE);

        binding.ratingProductProductitem.setRating(Float.valueOf(productResponse.ratings));

        if(productResponse.userID.equalsIgnoreCase(loginResponse.userID)){
            binding.btnFavouriteProductitem.setVisibility(View.GONE);
        }

        if (productResponse.liked != null){
            if (productResponse.liked.equals("1"))
                binding.btnFavouriteProductitem.setLiked(true);
            else
                binding.btnFavouriteProductitem.setLiked(false);
        }

        binding.btnFavouriteProductitem.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                updateLikeStatus(loginResponse.access_token, productResponse.productID, 1);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                updateLikeStatus(loginResponse.access_token, productResponse.productID, 0);
            }
        });
        return null;
    }

    private void updateLikeStatus(String accessToken, String productID, int status){
        Call<VerifyUserResponse> callUpdateLikeProduct =
                NetController.getInstance().getProductService().likeProduct(accessToken, productID, status);
        callUpdateLikeProduct.enqueue(new Callback<VerifyUserResponse>() {
            @Override
            public void onResponse(Call<VerifyUserResponse> call, Response<VerifyUserResponse> response) {
                if (response.code() == HTTP_OK){
                    SnackUtils.showSnackMessage(mActivity, response.body().msg);
                }else{
                    SnackUtils.showSnackMessage(mActivity, "Error in updating like");
                }
            }

            @Override
            public void onFailure(Call<VerifyUserResponse> call, Throwable t) {

            }
        });
    }


    @Override
    public Unit invoke(ProductResponse productResponse, Integer position) {
        Intent intent = new Intent(mActivity, ProductDetailActivity.class);
        intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
        startActivity(intent);
        /*View view = layoutManager.findViewByPosition(position);
        SimpleDraweeView imgProduct = (SimpleDraweeView) view.findViewById(R.id.imgProduct_productitem);
        intent.putExtra(AppConstants.KEYS.TRANSITION_VIEW.name(), ViewCompat.getTransitionName(imgProduct));
        intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                mActivity,
                imgProduct,
                ViewCompat.getTransitionName(imgProduct));

        startActivity(intent, options.toBundle());*/
        return null;
    }

    @Override
    public void setupViews() {
        loginResponse = ((SellerProfileActivity)mActivity).loginResponse;

        mProductAdapter = new RecyclerAdapterUtil<>(mActivity, mSellerProfile.productDetail, R.layout.product_item_layout);
        mProductAdapter.addOnDataBindListener(this);
        mProductAdapter.addOnClickListener(this);
        bi.recyclerSellerProducts.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        bi.recyclerSellerProducts.setLayoutManager(layoutManager);
        bi.recyclerSellerProducts.setAdapter(mProductAdapter);
    }
}
