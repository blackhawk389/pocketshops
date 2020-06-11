package com.appabilities.sold.screens.home.fragment.buy.view;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.appabilities.sold.BR;
import com.appabilities.sold.R;
import com.appabilities.sold.adapter.FeatureImagePagerAdapter;
import com.appabilities.sold.base.BaseFragment;
import com.appabilities.sold.databinding.FragmentBuyBinding;
import com.appabilities.sold.databinding.ProductItemLayoutBinding;
import com.appabilities.sold.model.SearchModel;
import com.appabilities.sold.networking.response.AdvertisementResponse;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.networking.response.VerifyUserResponse;
import com.appabilities.sold.screens.home.fragment.buy.BuyContract;
import com.appabilities.sold.screens.home.fragment.buy.BuyPresenter;
import com.appabilities.sold.screens.home.view.HomeActivity;
import com.appabilities.sold.screens.my_product_detail.view.MyProductDetailActivity;
import com.appabilities.sold.screens.product_detail.view.ProductDetailActivity;
import com.appabilities.sold.screens.show_products.view.ShowProductsActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jude.rollviewpager.OnItemClickListener;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class BuyFragment extends BaseFragment implements BuyContract.View {

    FragmentBuyBinding bi;
    BuyPresenter presenter;
    RecyclerAdapterUtil<ProductResponse> topProductsAdapter;
    RecyclerAdapterUtil<ProductResponse> reecentProductsAdapter;
    FeatureImagePagerAdapter sliderAdapter;
    ProductItemLayoutBinding bindingProductItem;
    private boolean isLoadedSlider = false, isLoadedTopProducts = false, isLoadedRecentProducts = false;
    private LoginResponse loginResponse;

    public BuyFragment() {
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
        // Inflate the layout for this fragment
        bi = DataBindingUtil.inflate(
                inflater, R.layout.fragment_buy, container, false);
        bi.setView(this);
        presenter = new BuyPresenter(this);
        bi.setPresenter(presenter);
        return bi.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        loginResponse = ((HomeActivity)getActivity()).loginResponse;
        presenter.getAdvertisement();
        presenter.getTopProducts(loginResponse.access_token);
        presenter.getRecentProducts(loginResponse.access_token);
    }

    @Override
    public void onMoreTopProductsClick() {
        SearchModel searchModel = new SearchModel();
        searchModel.setSortType("rating");
        Intent intent = new Intent(mActivity, ShowProductsActivity.class);
        intent.putExtra(AppConstants.KEYS.SEARCH_MODEL.name(), Parcels.wrap(searchModel));
        startActivity(intent);
    }

    @Override
    public void onMoreRecentProductsClick() {
        SearchModel searchModel = new SearchModel();
        searchModel.setSortType("date");
        Intent intent = new Intent(mActivity, ShowProductsActivity.class);
        intent.putExtra(AppConstants.KEYS.SEARCH_MODEL.name(), Parcels.wrap(searchModel));
        startActivity(intent);

    }

    @Override
    public void updateAdvertisement(final List<AdvertisementResponse> data, boolean status) {
        isLoadedSlider = true;
            if (data != null && data.size() == 1) {
                bi.sliderFeature.pause();
            }
            if (!bi.sliderFeature.isPlaying()) {
                sliderAdapter = new FeatureImagePagerAdapter(bi.sliderFeature, mActivity, data, status);
                bi.sliderFeature.setAdapter(sliderAdapter);
                bi.sliderFeature.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if(data != null) {
                            String url = data.get(position).url;
                            if (!url.isEmpty()) {
                                if (!url.startsWith("http://") && !url.startsWith("https://"))
                                    url = "http://" + url;
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(browserIntent);
                            }
                        }
                    }
                });
            } else
                sliderAdapter.refreshList(data);

            if (isLoadedSlider && isLoadedTopProducts && isLoadedRecentProducts) {
                showContent();
            }

    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.initScreen();
    }

    @Override
    public void advertisementError() {
        isLoadedSlider = true;
    }

    @Override
    public void updateTopProducts(List<ProductResponse> listProducts) {
        isLoadedTopProducts = true;
        topProductsAdapter = new RecyclerAdapterUtil<ProductResponse>(mActivity,
                listProducts, R.layout.product_item_layout);
        topProductsAdapter.addOnDataBindListener(new Function4<View, ProductResponse, Integer, Map<Integer, ? extends View>, Unit>() {
            @Override
            public Unit invoke(View view, final ProductResponse productResponse, final Integer position, Map<Integer, ? extends View> integerMap) {

                ProductItemLayoutBinding binding = DataBindingUtil.bind(view);
                binding.setProductModel(productResponse);
                binding.executePendingBindings();
                binding.ratingProductProductitem.setRating(Float.valueOf(productResponse.ratings));
                if (productResponse.userID.equals(((HomeActivity) mActivity).loginResponse.userID)) {
                    binding.btnFavouriteProductitem.setVisibility(View.GONE);
                } else {
                    if (productResponse.liked.equals("1"))
                        binding.btnFavouriteProductitem.setLiked(true);
                    else
                        binding.btnFavouriteProductitem.setLiked(false);
                }

                if(productResponse.userID.equalsIgnoreCase(loginResponse.userID)){
                    binding.btnFavouriteProductitem.setVisibility(View.GONE);
                }
                binding.slantedAuctionableProductitem.setVisibility(
                        Integer.parseInt(productResponse.auctionable) > 0 ? View.VISIBLE : View.INVISIBLE);

                binding.btnFavouriteProductitem.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        topProductsAdapter.getItemList().get(position).liked = "1";
                        topProductsAdapter.notifyDataSetChanged();
                        presenter.onUpdateProductLike(((HomeActivity) mActivity).loginResponse.access_token,
                                productResponse.productID, 1);
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        topProductsAdapter.getItemList().get(position).liked = "0";
                        topProductsAdapter.notifyDataSetChanged();
                        presenter.onUpdateProductLike(((HomeActivity) mActivity).loginResponse.access_token,
                                productResponse.productID, 0);
                    }
                });

                if (productResponse.sponsered.equals("1")) {
                    binding.txtStatusSponser.setVisibility(View.VISIBLE);
                }

                return null;
            }
        });

        bi.discreteTopProducts.setAdapter(topProductsAdapter);

        topProductsAdapter.addOnClickListener(new Function2<ProductResponse, Integer, Unit>() {
            @Override
            public Unit invoke(ProductResponse productResponse, Integer position) {

                Intent intent;
                if (productResponse.userID.equals(((HomeActivity) mActivity).loginResponse.userID)) {
                    intent = new Intent(mActivity, MyProductDetailActivity.class);
                } else {
                    intent = new Intent(mActivity, ProductDetailActivity.class);
                }
                intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
                startActivity(intent);
                /*int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    View view = bi.discreteTopProducts.getLayoutManager().findViewByPosition(position);
                    SimpleDraweeView imgProduct = (SimpleDraweeView) view.findViewById(R.id.imgProduct_productitem);
                    intent.putExtra(AppConstants.KEYS.TRANSITION_VIEW.name(), ViewCompat.getTransitionName(imgProduct));
                    intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            mActivity,
                            imgProduct,
                            ViewCompat.getTransitionName(imgProduct));

                    startActivity(intent, options.toBundle());
                } else {
                    intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
                    startActivity(intent);
                }*/
                return null;
            }
        });

        if (isLoadedSlider && isLoadedTopProducts && isLoadedRecentProducts) {
            showContent();
        }
    }

    @Override
    public void showEmptyTopProducts() {
        bi.layoutEmptyTop.setVisibility(View.VISIBLE);
        bi.discreteTopProducts.setVisibility(View.GONE);
        isLoadedTopProducts = true;
    }

    @Override
    public void showErrorTopProducts() {
      //  bi.layoutEmptyTop.setText("!! ERROR !!");
        bi.layoutEmptyTop.setText("No Internet Connection");
        bi.layoutEmptyTop.setVisibility(View.VISIBLE);
        bi.discreteTopProducts.setVisibility(View.GONE);
        isLoadedTopProducts = true;
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage("No Internet Connection.\nPlease connect to a network to use this app.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getActivity().finish();
                        System.exit(0);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void updateRecentProducts(List<ProductResponse> listProducts) {
        isLoadedRecentProducts = true;
        reecentProductsAdapter = new RecyclerAdapterUtil<ProductResponse>(mActivity,
                listProducts, R.layout.product_item_layout);
        reecentProductsAdapter.addOnDataBindListener(new Function4<View, ProductResponse, Integer, Map<Integer, ? extends View>, Unit>() {
            @Override
            public Unit invoke(View view, final ProductResponse productResponse, final Integer position, Map<Integer, ? extends View> integerMap) {

                bindingProductItem = DataBindingUtil.bind(view);
                bindingProductItem.setProductModel(productResponse);
                bindingProductItem.executePendingBindings();

                bindingProductItem.ratingProductProductitem.setRating(Float.valueOf(productResponse.ratings));
                if (productResponse.userID.equals(((HomeActivity) mActivity).loginResponse.userID)) {
                    bindingProductItem.btnFavouriteProductitem.setVisibility(View.GONE);
                } else {
                    if (productResponse.liked.equals("1"))
                        bindingProductItem.btnFavouriteProductitem.setLiked(true);
                }
                bindingProductItem.slantedAuctionableProductitem.setVisibility(
                        Integer.parseInt(productResponse.auctionable) > 0 ? View.VISIBLE : View.INVISIBLE);

                if(productResponse.userID.equalsIgnoreCase(loginResponse.userID)){
                    bindingProductItem.btnFavouriteProductitem.setVisibility(View.GONE);
                }

                bindingProductItem.btnFavouriteProductitem.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        reecentProductsAdapter.getItemList().get(position).liked = "1";
                        reecentProductsAdapter.notifyDataSetChanged();
                        presenter.onUpdateProductLike(((HomeActivity) mActivity).loginResponse.access_token,
                                productResponse.productID, 1);
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        reecentProductsAdapter.getItemList().get(position).liked = "0";
                        reecentProductsAdapter.notifyDataSetChanged();
                        presenter.onUpdateProductLike(((HomeActivity) mActivity).loginResponse.access_token,
                                productResponse.productID, 0);
                    }
                });

                if (productResponse.sponsered.equals("1")) {
                    bindingProductItem.txtStatusSponser.setVisibility(View.VISIBLE);
                }

                return null;
            }
        });
        bi.discreteRecentProducts.setAdapter(reecentProductsAdapter);

        reecentProductsAdapter.addOnClickListener(new Function2<ProductResponse, Integer, Unit>() {
            @Override
            public Unit invoke(ProductResponse productResponse, Integer position) {
                Intent intent;
                if (productResponse.userID.equals(((HomeActivity) mActivity).loginResponse.userID)) {
                    intent = new Intent(mActivity, MyProductDetailActivity.class);
                } else {
                    intent = new Intent(mActivity, ProductDetailActivity.class);
                }
                intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
                startActivity(intent);
                /*int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    View view = bi.discreteRecentProducts.getLayoutManager().findViewByPosition(position);
                    SimpleDraweeView imgProduct = (SimpleDraweeView) view.findViewById(R.id.imgProduct_productitem);
                    intent.putExtra(AppConstants.KEYS.TRANSITION_VIEW.name(), ViewCompat.getTransitionName(imgProduct));
                    intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            mActivity,
                            imgProduct,
                            ViewCompat.getTransitionName(imgProduct));

                    startActivity(intent, options.toBundle());
                } else {
                    intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
                    startActivity(intent);
                }*/

                return null;
            }
        });

        if (isLoadedSlider && isLoadedTopProducts && isLoadedRecentProducts) {
            showContent();
        }
    }

    @Override
    public void showEmptyRecentProducts() {
        bi.layoutEmptyRecent.setVisibility(View.VISIBLE);
        bi.discreteRecentProducts.setVisibility(View.GONE);
        isLoadedRecentProducts = true;
    }

    @Override
    public void showErrorRecentProducts() {
      //  bi.layoutEmptyRecent.setText("!! ERROR !!");
        bi.layoutEmptyRecent.setText("No Internet Connection");
        bi.layoutEmptyRecent.setVisibility(View.VISIBLE);
        bi.discreteRecentProducts.setVisibility(View.GONE);
        isLoadedRecentProducts = true;
//        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
//                .setMessage("No Internet Connectivity")
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();
    }

    @Override
    public void showContent() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
    }

    @Override
    public void updateProductLikeStatus(VerifyUserResponse body, int status) {
        if (status == 0)
            SnackUtils.showSnackMessage(mActivity, "Un-Liked");
        else
            SnackUtils.showSnackMessage(mActivity, "Liked");
    }

    @Override
    public void errorUpdateLikeStatus() {
        //bindingProductItem.btnFavouriteProductitem.setLiked(false);
        SnackUtils.showSnackMessage(mActivity, "This action is not performed");
    }
}
