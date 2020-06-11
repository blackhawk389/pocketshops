package com.appabilities.sold.screens.user_profile.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appabilities.sold.R;
import com.appabilities.sold.base.FragmentLifecycle;
import com.appabilities.sold.database.SoldDatabase;
import com.appabilities.sold.database.tables.RelationshipModel;
import com.appabilities.sold.model.GetFollowers;
import com.appabilities.sold.model.UserModel;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.screens.seller_profile.view.SellerProfileActivity;
import com.appabilities.sold.utils.AppConstants;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Wajahat on 9/30/2016.
 */

public class UsersListFragment extends Fragment implements FragmentLifecycle {

    public final String TAG = UsersListFragment.class.getSimpleName();

    @BindView(R.id.recyclerUsers_userslistfrag)
    RecyclerView recyclerUsers;
    @BindView(R.id.progressMyProducts_myproducts)
    ProgressBar progressMyProducts;
    @BindView(R.id.layoutEmptyState_myproducts)
    LinearLayout layoutEmptyState;
//    @BindView(R.id.refresh_myproducts)
//    SwipeRefreshLayout refreshMyProducts;
    @BindView(R.id.no_found)
    TextView follower;

    public AppConstants.USER_TYPE usersType = AppConstants.USER_TYPE.NONE;
    public UsersRecylcerAdapter listAdapter;
    public String userId;
    ArrayList<UserModel> obj;
    boolean isInitialized = false;
    boolean isUserUpdated = false;
    boolean isFollower;

    public UsersListFragment(ArrayList<UserModel> obj,boolean isFollower) {
        this.obj = obj;
        this.isFollower = isFollower;
        // Required empty public constructor
    }
    public UsersListFragment() {

        // Required empty public constructor
    }
//    public static UsersListFragment newInstance(AppConstants.USER_TYPE utype, String uId) {
////        UsersListFragment fragment = new UsersListFragment();
////        Bundle args = new Bundle();
////        args.putSerializable(AppConstants.BUNDLE_KEYS.KEY_USER_TYPE, utype);
////        args.putString("userId", uId);
////        //args.putSerializable("serviceObject", ado);
////        fragment.setArguments(args);
////        return fragment;
//
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null) {
//            usersType = (AppConstants.USER_TYPE) getArguments().getSerializable(AppConstants.BUNDLE_KEYS.KEY_USER_TYPE);
//            userId = getArguments().getString("userId");
//            //serviceObject = (ActivityDetailsObject) getArguments().getSerializable("serviceObject");
//        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_users_list_layout, container, false);
        ButterKnife.bind(this, rootView);

        if(obj != null && obj.size() > 0) {

            listAdapter = new UsersRecylcerAdapter(getActivity(), this, obj);

        /*
        if (usersType == AppConstants.USER_TYPE.FOLLOWER)
        {
            listAdapter.usersList = loginresponse.getFollowersList();
        }
        else {
            listAdapter.usersList = loginresponse.getFollowingsList();
        }
        */

            recyclerUsers.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerUsers.setLayoutManager(layoutManager);
            recyclerUsers.setAdapter(listAdapter);

//            refreshMyProducts.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    //refreshLocalData();
//                }
//            });

            layoutEmptyState.setVisibility(View.GONE);
            progressMyProducts.setVisibility(View.GONE);

            listAdapter.notifyDataSetChanged();
        }else{
            if(isFollower){
                follower.setText("No Followers found");
            }else{
                follower.setText("No Following found");
            }
            layoutEmptyState.setVisibility(View.VISIBLE);
            progressMyProducts.setVisibility(View.GONE);
            recyclerUsers.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!isInitialized)
        {
            Log.w(TAG, "onViewCreated: Loading for once only" );
            isInitialized = true;
            // Reload data
           // refreshLocalData();
            //getDataFromServer();
        }

    }

    /*
    // Original
    public void refreshLocalData()
    {
        if (usersType == AppConstants.USER_TYPE.FOLLOWER)
        {
            listAdapter.usersList = AppDatabase.getFollowersList(userId);
            listAdapter.notifyDataSetChanged();
        }
        else {
            listAdapter.usersList = AppDatabase.getFollowingList(userId);
            listAdapter.notifyDataSetChanged();
        }

        if (listAdapter.usersList.size() > 0)
            layoutEmptyState.setVisibility(View.GONE);
        else {
            layoutEmptyState.setVisibility(View.VISIBLE);
        }
    }*/

//    public void refreshLocalData()
//    {
//        refreshMyProducts.post(new Runnable() {
//            @Override
//            public void run() {
//                refreshMyProducts.setRefreshing(true);
//                // directly call onRefresh() method
//            }
//        });
//
//        if (usersType == AppConstants.USER_TYPE.FOLLOWER)
//        {
//            //SoldDatabase.getFollowing();
//            SoldDatabase.getFollowersListQuery(userId)
//                    .async()
//                    .queryListResultCallback(new QueryTransaction.QueryResultListCallback<UserModel>() {
//                        @Override
//                        public void onListQueryResult(QueryTransaction transaction, @Nullable List<UserModel> tResult) {
//                            refreshMyProducts.setRefreshing(false);
//
//                            listAdapter.usersList.clear();
//                            listAdapter.usersList.addAll(tResult);
//                            listAdapter.notifyDataSetChanged();
//
//                            if (listAdapter.usersList.size() > 0)
//                                layoutEmptyState.setVisibility(View.GONE);
//                            else {
//                                layoutEmptyState.setVisibility(View.VISIBLE);
//                            }
//                        }
//                    })
//                    .execute();
//
//            //listAdapter.usersList = AppDatabase.getFollowersList(userId);
//            //listAdapter.notifyDataSetChanged();
//        }
//        else {
//            //listAdapter.usersList = AppDatabase.getFollowingList(userId);
//            //listAdapter.notifyDataSetChanged();
//
//            SoldDatabase.getFollowingListQuery(userId)
//                    .async()
//                    .queryListResultCallback(new QueryTransaction.QueryResultListCallback<UserModel>() {
//                        @Override
//                        public void onListQueryResult(QueryTransaction transaction, @Nullable List<UserModel> tResult) {
//                            refreshMyProducts.setRefreshing(false);
//
//                            listAdapter.usersList.clear();
//                            listAdapter.usersList.addAll(tResult);
//                            listAdapter.notifyDataSetChanged();
//
//
//                            if (listAdapter.usersList.size() > 0)
//                                layoutEmptyState.setVisibility(View.GONE);
//                            else {
//                                layoutEmptyState.setVisibility(View.VISIBLE);
//                            }
//                        }
//                    })
//                    .execute();
//
//        }
//    }

//    public void refreshLocalData(String uid)
//    {
//        if (listAdapter == null) return;
//
//        userId = uid;
//        if (usersType == AppConstants.USER_TYPE.FOLLOWER)
//        {
//            listAdapter.usersList = SoldDatabase.getFollowersList(userId);
//            listAdapter.notifyDataSetChanged();
//        }
//        else {
//            listAdapter.usersList = SoldDatabase.getFollowingList(userId);
//            listAdapter.notifyDataSetChanged();
//        }
//
//        if (listAdapter.usersList.size() > 0)
//            layoutEmptyState.setVisibility(View.GONE);
//        else {
//            layoutEmptyState.setVisibility(View.VISIBLE);
//        }
//    }

    /*public void getDataFromServer()
    {
        // Check if the user is not logged in user.
        LoginResponse login = SQLite.select().from(LoginResponse.class).querySingle();
        if (!userId.equals(login.userID))
        {
            refreshMyProducts.post(new Runnable() {
                @Override
                public void run() {
                    refreshMyProducts.setRefreshing(true);
                    // directly call onRefresh() method
                }
            });


            Call<UserModel> call = NetController.getInstance().getUserService().getSellerProfile(userId);
            call.enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                    recyclerUsers.setVisibility(View.VISIBLE);
                    refreshMyProducts.setRefreshing(false);

                    if (response.code() == HTTP_OK) {

                        UserModel thisUser = response.body();
                        thisUser.save();

                        // Followers
                        for (int kk = 0; kk < thisUser.followers_detail.size(); kk++) {

                            UserModel temp = thisUser.followers_detail.get(kk);
                            //temp.type = AppConstants.USER_TYPE.FOLLOWER;
                            temp.save();

                            RelationshipModel relation = new RelationshipModel();
                            relation.followerId = temp.userID;
                            relation.followingId = thisUser.userID;
                            relation.relationshipId = relation.followerId + "" + relation.followingId;
                            relation.textValue = temp.display_name + " --> " + thisUser.display_name;
                            relation.save();
                        }

                        // Following
                        for (int kk = 0; kk < thisUser.following_detail.size(); kk++) {

                            UserModel temp = thisUser.following_detail.get(kk);
                            //temp.type = AppConstants.USER_TYPE.FOLLOWING;
                            temp.save();

                            RelationshipModel relation = new RelationshipModel();
                            relation.followerId = thisUser.userID;
                            relation.followingId = temp.userID;
                            relation.relationshipId = relation.followerId + "" + relation.followingId;
                            relation.textValue = thisUser.display_name + " --> " + temp.display_name;
                            relation.save();
                        }

                        refreshLocalData();
                        listAdapter.notifyDataSetChanged();
                    }
                    else if (response.code() == HTTP_NOT_FOUND)
                    {
                        recyclerUsers.setVisibility(View.GONE);
                        layoutEmptyState.setVisibility(View.VISIBLE);
                    }
                    else {
                        recyclerUsers.setVisibility(View.GONE);
                        layoutEmptyState.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onFailure(Call<UserModel> call, Throwable t) {
                    t.printStackTrace();
                    //progressMyProducts.setVisibility(View.GONE);
                    refreshMyProducts.setRefreshing(false);
                    recyclerUsers.setVisibility(View.VISIBLE);
                }
            });
        }
        else
        {
            refreshMyProducts.setRefreshing(false);
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPauseFragment() {

        Log.e(TAG, "onPauseFragment: Paused " + usersType );
    }

    @Override
    public void onResumeFragment() {

        Log.d(TAG, "onResumeFragment: Resume" + usersType);

        if (listAdapter != null)
        {
            /*
            //LoginResponse loginresponse = SQLite.select().from(LoginResponse.class).querySingle();
            if (usersType == AppConstants.USER_TYPE.FOLLOWER)
            {
                listAdapter.usersList = AppDatabase.getFollowersList(userId);
                listAdapter.notifyDataSetChanged();
            }
            else {
                listAdapter.usersList = AppDatabase.getFollowingList(userId);
                listAdapter.notifyDataSetChanged();
            }

            if (listAdapter.usersList.size() > 0)
                layoutEmptyState.setVisibility(View.GONE);
            else {
                layoutEmptyState.setVisibility(View.VISIBLE);
            }
            */
           // refreshLocalData();
        }
    }
}