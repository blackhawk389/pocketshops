package com.appabilities.sold.screens.home.fragment.favourites.view;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseFragment;
import com.appabilities.sold.databinding.FragmentFavouriteBinding;
import com.appabilities.sold.databinding.ProductItemLayoutBinding;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.networking.response.VerifyUserResponse;
import com.appabilities.sold.screens.home.fragment.favourites.FavouriteContract;
import com.appabilities.sold.screens.home.fragment.favourites.FavouritePresenter;
import com.appabilities.sold.screens.home.view.HomeActivity;
import com.appabilities.sold.screens.product_detail.view.ProductDetailActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kennyc.view.MultiStateView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;


import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends BaseFragment implements FavouriteContract.View<List<ProductResponse>>, Function2<ProductResponse, Integer, Unit>, Function4<View, ProductResponse, Integer, Map<Integer, ? extends View>, Unit> {

    FragmentFavouriteBinding bi;
    FavouritePresenter presenter;
    LinearLayoutManager layoutManager;

    RecyclerAdapterUtil<ProductResponse> favAdapter;
    List<ProductResponse> favProductList;
    private String accessToken;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bi = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite, container, false);
        bi.setView(this);
        presenter = new FavouritePresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
        return bi.getRoot();
    }

    @Override
    public void setupViews() {
        accessToken = ((HomeActivity)mActivity).getLoginResponse().access_token;
        favProductList = new ArrayList<>();
        favAdapter = new RecyclerAdapterUtil<>(mActivity, favProductList, R.layout.product_item_layout);
        favAdapter.addOnDataBindListener(this);
        favAdapter.addOnClickListener(this);
        bi.favRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        bi.favRecyclerView.setLayoutManager(layoutManager);
        bi.favRecyclerView.setAdapter(favAdapter);
        presenter.getUserFavouriteProduct(((HomeActivity)mActivity).getLoginResponse().access_token);

    }
    @Override
    public void onResume() {
        super.onResume();
        presenter.initScreen();
    }

    @Override
    public void onLoading() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void onRequestSuccessful(List<ProductResponse> responseData) {
        favAdapter.getItemList().removeAll(favAdapter.getItemList());
        favAdapter.getItemList().addAll(responseData);
        favAdapter.notifyDataSetChanged();
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
    public void updateProductLikeStatus(VerifyUserResponse body, int status) {
        SnackUtils.showSnackMessage(mActivity, Integer.toString(status).equalsIgnoreCase("0") ? "Unliked" : "Liked");
        presenter.getUserFavouriteProduct(((HomeActivity)mActivity).getLoginResponse().access_token);
    }

    @Override
    public void errorUpdateLikeStatus() {
        SnackUtils.showSnackMessage(mActivity,"Error!");
    }

    @Override
    public Unit invoke(ProductResponse productResponse, Integer position) {
        View view = layoutManager.findViewByPosition(position);
        SimpleDraweeView imgProduct = (SimpleDraweeView) view.findViewById(R.id.imgProduct_productitem);
        Intent intent = new Intent(mActivity, ProductDetailActivity.class);
        intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
        startActivity(intent);
        /*int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            intent.putExtra(AppConstants.KEYS.TRANSITION_VIEW.name(), ViewCompat.getTransitionName(imgProduct));
            intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    mActivity,
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
    public Unit invoke(View view, final ProductResponse productResponse, final Integer position, Map<Integer, ? extends View> integerMap) {
        ProductItemLayoutBinding binding = DataBindingUtil.bind(view);
        binding.setProductModel(productResponse);
        binding.executePendingBindings();
        binding.ratingProductProductitem.setRating(Float.valueOf(productResponse.ratings));
       //binding.btnFavouriteProductitem.setLiked(true);
        ViewCompat.setTransitionName(binding.imgProductProductitem, productResponse.imgName);
        if(productResponse.isSelected){
            binding.btnFavouriteProductitem.setLiked(true);
        }else{
            binding.btnFavouriteProductitem.setLiked(false);
        }

        if (productResponse.auctionable.equals("1"))
            binding.slantedAuctionableProductitem.setVisibility(View.VISIBLE);

        if (productResponse.sponsered.equals("1"))
            binding.txtStatusSponser.setVisibility(View.VISIBLE);

        binding.btnFavouriteProductitem.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                favAdapter.getItemList().remove(position);
                favAdapter.notifyItemRemoved(position);
                productResponse.isSelected = true;
                presenter.onUpdateProductLike(accessToken, productResponse.productID, 1);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                favAdapter.getItemList().remove(position);
                favAdapter.notifyItemRemoved(position);
                productResponse.isSelected = false;
                presenter.onUpdateProductLike(accessToken, productResponse.productID, 0);
            }
        });
        return null;
    }
}
