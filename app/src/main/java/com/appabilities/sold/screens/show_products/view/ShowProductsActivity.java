package com.appabilities.sold.screens.show_products.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivityShowProductsBinding;
import com.appabilities.sold.databinding.ProductItemLayoutBinding;
import com.appabilities.sold.model.SearchModel;
import com.appabilities.sold.networking.response.CategoriesResponse;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.networking.response.SubCategoryResponse;
import com.appabilities.sold.networking.response.VerifyUserResponse;
import com.appabilities.sold.screens.filters.view.FiltersActivity;
import com.appabilities.sold.screens.my_product_detail.view.MyProductDetailActivity;
import com.appabilities.sold.screens.product_detail.view.ProductDetailActivity;
import com.appabilities.sold.screens.show_products.ShowProductsContract;
import com.appabilities.sold.screens.show_products.ShowProductsPresenter;
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

public class ShowProductsActivity extends BaseActivity implements ShowProductsContract.View<List<ProductResponse>>, Function4<View, ProductResponse, Integer, Map<Integer, ? extends View>, Unit>, Function2<ProductResponse, Integer, Unit> {

    private ActivityShowProductsBinding bi;
    private ShowProductsPresenter presenter;
    private SubCategoryResponse subCategoryItem;

    private boolean isFromCategory = false;
    private RecyclerAdapterUtil<ProductResponse> mProductAdapter;
    private List<ProductResponse> listProducts;
    private LinearLayoutManager layoutManager;
    private String searchText = "", minPrice = "", maxPrice = "",
            sortBy = "", categoryID = "", subcategoryID = "", sellerName = "", categoryName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_show_products);
        presenter = new ShowProductsPresenter(this);
        bi.setView(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listProducts = new ArrayList<>();
        isFromCategory = getIntent().getExtras().getBoolean(AppConstants.KEYS.IS_FROM_CATEGORY.name());
        categoryID = getIntent().getExtras().getString(AppConstants.KEYS.CATEGORY_ID.name(),"");
        categoryName = getIntent().getExtras().getString(AppConstants.KEYS.CATEGORY_NAME.name());


        mProductAdapter = new RecyclerAdapterUtil<>(this, listProducts, R.layout.product_item_layout);
        mProductAdapter.addOnDataBindListener(this);
        mProductAdapter.addOnClickListener(this);
        bi.recyclerProducts.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        bi.recyclerProducts.setLayoutManager(layoutManager);
        bi.recyclerProducts.setAdapter(mProductAdapter);

        if (isFromCategory){
            getSupportActionBar().setTitle(categoryName);
        }else {
            if (getIntent().hasExtra(AppConstants.KEYS.SUB_CATEGORY_ITEM.name())){
                subCategoryItem = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.SUB_CATEGORY_ITEM.name()));
                getSupportActionBar().setTitle(subCategoryItem.categoryName);
                subcategoryID = subCategoryItem.categoryID;
            }else if (getIntent().hasExtra(AppConstants.KEYS.SEARCH_MODEL.name())){
                SearchModel searchModel = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.SEARCH_MODEL.name()));
                searchText = searchModel.searchText;
                minPrice = String.valueOf(searchModel.minRange);
                maxPrice = String.valueOf(searchModel.maxRange);
                sortBy = searchModel.sortType;
                if (searchModel.categoryID != -1)
                    categoryID = String.valueOf(searchModel.categoryID);
                if (searchModel.subCategoryID != -1)
                    subcategoryID = String.valueOf(searchModel.subCategoryID);
                sellerName = searchModel.searchSellerName;
                if (sortBy.equals("rating")){
                    getSupportActionBar().setTitle("TOP PRODUCTS");
                }else if (sortBy.equals("date")){
                    getSupportActionBar().setTitle("RECENT PRODUCTS");
                }else {
                    getSupportActionBar().setTitle("FILTERED PRODUCTS");
                }
            }


        }
        callProducts(searchText, minPrice, maxPrice, sortBy, categoryID, subcategoryID, sellerName, loginResponse.access_token);
    }

    @Override
    public void onLoading() {
        bi.multiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void onRequestSuccessful(List<ProductResponse> responseData) {
        mProductAdapter.getItemList().clear();
        mProductAdapter.getItemList().addAll(responseData);
        mProductAdapter.notifyDataSetChanged();
        bi.multiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
    }

    @Override
    public void onRequestFail(String errorMessage) {
        SnackUtils.showErrorMessage(this, errorMessage);
        bi.multiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void onRequestNotFound() {
        bi.multiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
    }

    @Override
    public void callProducts(String searchText, String minPrice, String maxPrice,
                             String sortBy, String categoryID, String subCategoryID, String sellerName, String accessToken) {
        presenter.getProducts(searchText, minPrice, maxPrice, sortBy, categoryID, subCategoryID, sellerName, accessToken);
    }

    @Override
    public void updateProductLikeStatus(VerifyUserResponse body, int status) {
        if (status == 1)
            SnackUtils.showSnackMessage(this, "Liked", true);
        else
            SnackUtils.showSnackMessage(this, "UnLiked", true);
    }

    @Override
    public void errorUpdateLikeStatus(String errorMsg) {
        SnackUtils.showErrorMessage(this, errorMsg);
    }

    @Override
    public void onClickFilters() {

        Intent intent = new Intent(ShowProductsActivity.this, FiltersActivity.class);
        intent.putExtra("category", categoryID);
        intent.putExtra("subcategory", subcategoryID);
        intent.putExtra("sort", sortBy);
        intent.putExtra("minPrice", minPrice);
        intent.putExtra("maxPrice", maxPrice);
        startActivityForResult(intent, 1001);
    }

    @Override
    public Unit invoke(View view, final ProductResponse productResponse, Integer position, Map<Integer, ? extends View> integerMap) {
        ProductItemLayoutBinding binding = DataBindingUtil.bind(view);
        binding.setProductModel(productResponse);
        binding.executePendingBindings();

        if (productResponse.userID.equals(loginResponse.userID)){
            binding.btnFavouriteProductitem.setVisibility(View.GONE);
        }else {
            if (productResponse.liked.equals("1"))
                binding.btnFavouriteProductitem.setLiked(true);
            else
                binding.btnFavouriteProductitem.setLiked(false);
        }
        if (productResponse.auctionable.equals("1"))
            binding.slantedAuctionableProductitem.setVisibility(View.VISIBLE);
        else
            binding.slantedAuctionableProductitem.setVisibility(View.INVISIBLE);

        if (productResponse.sponsered.equals("1"))
            binding.txtStatusSponser.setVisibility(View.VISIBLE);
        else
            binding.txtStatusSponser.setVisibility(View.INVISIBLE);

        binding.ratingProductProductitem.setRating(Float.valueOf(productResponse.ratings));
        binding.btnFavouriteProductitem.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                presenter.onUpdateProductLike(loginResponse.access_token, productResponse.productID,
                        1);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                presenter.onUpdateProductLike(loginResponse.access_token, productResponse.productID,
                        0);
            }
        });

        return null;
    }

    @Override
    public Unit invoke(ProductResponse productResponse, Integer position) {
        Intent intent;
        if (productResponse.userID.equals(loginResponse.userID)){
            intent = new Intent(this, MyProductDetailActivity.class);
        }else {
            intent = new Intent(this, ProductDetailActivity.class);
        }
        intent.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(productResponse));
        startActivity(intent);
        /*int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            View view = layoutManager.findViewByPosition(position);
            SimpleDraweeView imgProduct = (SimpleDraweeView) view.findViewById(R.id.imgProduct_productitem);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            minPrice = data.getStringExtra("minPrice");
            maxPrice = data.getStringExtra("maxPrice");
            sortBy = data.getStringExtra("selectedSort");
            CategoriesResponse categoriesResponse = Parcels.unwrap(data.getParcelableExtra("selectedCategory"));
            SubCategoryResponse subCategoryResponse = Parcels.unwrap(data.getParcelableExtra("selectedSubCategory"));
            if (categoriesResponse == null || categoriesResponse.categoryName.equals("Any")){
                categoryID = "";
                getSupportActionBar().setTitle("FILTERED PRODUCTS");
            }
            else{
                categoryID = categoriesResponse.categoryID;
                getSupportActionBar().setTitle(categoriesResponse.categoryName);
            }
            if (subCategoryResponse == null ||
                    subCategoryResponse.categoryName.equals("Any") ||
                    subCategoryResponse.categoryName.equals("None")){
                subcategoryID = "";
            }else {
                subcategoryID = subCategoryResponse.categoryID;
                getSupportActionBar().setTitle(subCategoryResponse.categoryName);
            }
            presenter.getProducts("", minPrice, maxPrice,sortBy, categoryID, subcategoryID, "", loginResponse.access_token);
        }
    }
}
