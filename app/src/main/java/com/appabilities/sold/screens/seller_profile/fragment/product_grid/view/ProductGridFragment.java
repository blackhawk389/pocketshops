package com.appabilities.sold.screens.seller_profile.fragment.product_grid.view;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseFragment;
import com.appabilities.sold.databinding.FragmentProductGridBinding;
import com.appabilities.sold.databinding.ProductGridItemLayoutBinding;
import com.appabilities.sold.databinding.ProductItemLayoutBinding;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.networking.response.SellerProfileResponse;
import com.appabilities.sold.networking.response.VerifyUserResponse;
import com.appabilities.sold.screens.product_detail.view.ProductDetailActivity;
import com.appabilities.sold.screens.seller_profile.view.SellerProfileActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kennyc.view.MultiStateView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;

import org.parceler.Parcels;

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
public class ProductGridFragment extends BaseFragment implements Function2<ProductResponse, Integer, Unit>, Function4<View, ProductResponse, Integer, Map<Integer, ? extends View>, Unit> {

    RecyclerView.LayoutManager mLayoutManager;
    FragmentProductGridBinding bi;
    RecyclerAdapterUtil<ProductResponse> mProductAdapter;
    LoginResponse loginResponse;
    private static final String ARG_PARAM = "param1";
    SellerProfileResponse mSellerResponse;

    public static ProductGridFragment newInstance(SellerProfileResponse mSellerResponse){
        ProductGridFragment productGridFragment = new ProductGridFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM, Parcels.wrap(mSellerResponse));
        productGridFragment.setArguments(args);
        return productGridFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mSellerResponse = Parcels.unwrap(getArguments().getParcelable(ARG_PARAM));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (bi== null) {
            bi = DataBindingUtil.inflate(inflater, R.layout.fragment_product_grid, container, false);
            setUpViews();
        }
        return bi.getRoot();
    }

    private void setUpViews(){
        loginResponse = ((SellerProfileActivity)mActivity).loginResponse;
        if (mSellerResponse.productDetail == null || mSellerResponse.productDetail.size() == 0){
            //bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
            return;
        }
        mProductAdapter = new RecyclerAdapterUtil<>(mActivity, mSellerResponse.productDetail, R.layout.product_grid_item_layout);
        mProductAdapter.addOnDataBindListener(this);
        mProductAdapter.addOnClickListener(this);
        mLayoutManager = new GridLayoutManager(mActivity, 2);
        bi.recyclerSellerProducts.setLayoutManager(mLayoutManager);
        bi.recyclerSellerProducts.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(4), true));
        bi.recyclerSellerProducts.setItemAnimator(new DefaultItemAnimator());
        bi.recyclerSellerProducts.setAdapter(mProductAdapter);
    }

    @Override
    public Unit invoke(ProductResponse productResponse, Integer position) {
        Intent intent = new Intent(mActivity, ProductDetailActivity.class);
        intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
        startActivity(intent);
        /*View view = mLayoutManager.findViewByPosition(position);
        SimpleDraweeView imgProduct = (SimpleDraweeView) view.findViewById(R.id.img_product_grid);
        ViewCompat.setTransitionName(imgProduct, "GRID_IMG");
        intent.putExtra(AppConstants.KEYS.TRANSITION_VIEW.name(), "GRID_IMG");
        intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                mActivity,
                imgProduct,
                "GRID_IMG");

        startActivity(intent, options.toBundle());*/
        return null;
    }

    @Override
    public Unit invoke(View view, final ProductResponse productResponse, Integer integer, Map<Integer, ? extends View> integerMap) {
        ProductGridItemLayoutBinding binding = DataBindingUtil.bind(view);
        binding.setProductItem(productResponse);
        binding.executePendingBindings();

        if (productResponse.auctionable.equals("1"))
            binding.slantedAuctionableProductitem.setVisibility(View.VISIBLE);
        else
            binding.slantedAuctionableProductitem.setVisibility(View.GONE);

        if (productResponse.liked != null){
            if (productResponse.liked.equals("1"))
                binding.likeButtonProductitem.setLiked(true);
            else
                binding.likeButtonProductitem.setLiked(false);
        }

        if(productResponse.userID.equalsIgnoreCase(loginResponse.userID)){
            binding.likeButtonProductitem.setVisibility(View.GONE);
        }

        binding.likeButtonProductitem.setOnLikeListener(new OnLikeListener() {
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

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
