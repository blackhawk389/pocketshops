package com.appabilities.sold.screens.search_seller.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appabilities.sold.R;
import com.appabilities.sold.base.EndlessRecyclerViewScrollListener;
import com.appabilities.sold.base.sidemenu.SideMenuBaseActivity;
import com.appabilities.sold.database.SoldDatabase;
import com.appabilities.sold.database.tables.RelationshipModel;
import com.appabilities.sold.databinding.ActivitySearchSellerBinding;
import com.appabilities.sold.databinding.UserItemLayoutBinding;
import com.appabilities.sold.model.UserModel;
import com.appabilities.sold.networking.response.SellerResponse;
import com.appabilities.sold.networking.response.VerifyUserResponse;
import com.appabilities.sold.screens.search_seller.SearchSellerContract;
import com.appabilities.sold.screens.search_seller.SearchSellerPresenter;
import com.appabilities.sold.screens.seller_profile.view.SellerProfileActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.kennyc.view.MultiStateView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;

public class SearchSellerActivity extends SideMenuBaseActivity implements
        SearchSellerContract.View<List<SellerResponse>>,
        Function4<View, SellerResponse, Integer, Map<Integer, ? extends View>, Unit>,
        Function2<SellerResponse, Integer, Unit> {

    private static final String TAG = SearchSellerActivity.class.getSimpleName();
    ActivitySearchSellerBinding bi;
    SearchSellerPresenter presenter;
    RecyclerAdapterUtil<SellerResponse> mRecyclerAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    LinearLayoutManager layoutManager;
    String searchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = (ActivitySearchSellerBinding) setContentLayout(R.layout.activity_search_seller);
        bi.setView(this);
        presenter = new SearchSellerPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        initDrawer(bi.toolbar, "SEARCH SELLER");
        mRecyclerAdapter = new RecyclerAdapterUtil<>(this, new ArrayList<SellerResponse>(), R.layout.user_item_layout);
        mRecyclerAdapter.addOnDataBindListener(this);
        mRecyclerAdapter.addOnClickListener(this);
        bi.recyclerSeller.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        bi.recyclerSeller.setLayoutManager(layoutManager);
        bi.recyclerSeller.setAdapter(mRecyclerAdapter);


        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (page < 11)
                    getSellers("" + page);
            }
        };
        bi.recyclerSeller.addOnScrollListener(scrollListener);

        getSellers("0");
        bi.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchText = query;
                Log.i(TAG, "onQueryTextSubmit: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                presenter.getSellers(loginResponse.access_token, "0", searchText);
                Log.i(TAG, "onQueryTextChange: " + newText);
                return false;
            }
        });
    }

    @Override
    public void onLoading() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void onRequestSuccessful(List<SellerResponse> responseData) {
        if (!searchText.isEmpty()) {
            mRecyclerAdapter.getItemList().clear();
        }
        mRecyclerAdapter.getItemList().addAll(responseData);
        mRecyclerAdapter.notifyDataSetChanged();
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
    }

    @Override
    public void onRequestFail(String errorMessage) {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
    }

    @Override
    public void onRequestNotFound() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
    }

    @Override
    public void getSellers(String offset) {
        if(mRecyclerAdapter.getItemCount() == 0) {
            presenter.getSellers(loginResponse.access_token, offset, searchText);
        }
    }

    @Override
    public void updateFollowStatus(VerifyUserResponse body) {
        SnackUtils.showSnackMessage(SearchSellerActivity.this, "Status Updated Successfully");
    }

    @Override
    public void errorFollowStatus() {
        SnackUtils.showSnackMessage(SearchSellerActivity.this, "Error in updating status");
    }

    @Override
    public Unit invoke(View view, final SellerResponse sellerResponse, Integer integer, Map<Integer, ? extends View> integerMap) {
        UserItemLayoutBinding binding = DataBindingUtil.bind(view);
        binding.setSellerModel(sellerResponse);
        binding.executePendingBindings();
        binding.txtLocationUseritem.setText(!sellerResponse.region.isEmpty() ? sellerResponse.region + "," + sellerResponse.country : sellerResponse.country);

        if (sellerResponse.isFollowing.equals("0")){
            binding.btnFollowUseritem.setLiked(false);
        } else{
            binding.btnFollowUseritem.setLiked(true);
        }

        if(sellerResponse.userID.equalsIgnoreCase(loginResponse.userID)){
            binding.btnFollowUseritem.setVisibility(View.GONE);
        }


        binding.btnFollowUseritem.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                UserModel userModel = new UserModel();
                userModel.saveFollowing(sellerResponse);

                RelationshipModel relation = new RelationshipModel();
                relation.followerId = loginResponse.userID;
                relation.followingId = sellerResponse.userID;
                relation.relationshipId = relation.followerId + "" + relation.followingId;
                relation.textValue = loginResponse.display_name + " --> " + sellerResponse.displayName;
                relation.save();

                presenter.updateFollowStatus(loginResponse.access_token,
                        AppConstants.VALUES.FOLLOWING,
                        sellerResponse.userID,
                        AppConstants.VALUES.STATUS_FOLLOW);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                String relationID = loginResponse.userID+""+sellerResponse.userID;
                RelationshipModel relationship = SoldDatabase.getRelation(relationID);
                relationship.delete();
                UserModel userModel = SoldDatabase.getUser(sellerResponse.userID);
                userModel.delete();
                presenter.updateFollowStatus(loginResponse.access_token,
                        AppConstants.VALUES.FOLLOWING,
                        sellerResponse.userID,
                        AppConstants.VALUES.STATUS_UNFOLLOW);
            }
        });
        return null;
    }

    @Override
    public Unit invoke(SellerResponse sellerResponse, Integer position) {
        Intent intent = new Intent(SearchSellerActivity.this, SellerProfileActivity.class);
        intent.putExtra(AppConstants.KEYS.SELLER_ID.name(), sellerResponse.userID);
        startActivity(intent);
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        selectMenuOption(R.id.nav_seller);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        bi.searchView.setMenuItem(item);
        return true;
    }
}
