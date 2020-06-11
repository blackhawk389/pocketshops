package com.appabilities.sold.screens.user_profile.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.appabilities.sold.R;
import com.appabilities.sold.database.tables.RelationshipModel;
import com.appabilities.sold.database.tables.RelationshipModel_Table;
import com.appabilities.sold.model.GetFollowers;
import com.appabilities.sold.model.UserModel;
import com.appabilities.sold.model.UserModel_Table;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.networking.response.VerifyUserResponse;
import com.appabilities.sold.screens.seller_profile.view.SellerProfileActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.rest.Get;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.facebook.stetho.server.http.HttpStatus.HTTP_OK;

/**
 * Created by Wajahat on 9/30/2016.
 */

public class UsersRecylcerAdapter extends RecyclerView.Adapter<UsersRecylcerAdapter.ViewHolder> {

    public List<UserModel> usersList = new ArrayList<>();
    private Context context;
    public AppConstants.USER_TYPE usersType = AppConstants.USER_TYPE.NONE;
    public UsersListFragment usersFragment;
    public LoginResponse loginResponse;
    ArrayList<UserModel> obj;

    public UsersRecylcerAdapter(Context ctx,  UsersListFragment fragment, ArrayList<UserModel> obj)
    {
        context = ctx;
       // usersType = utype;
        usersFragment = fragment;
        this.obj= obj;
        //loginResponse = SQLite.select().from(LoginResponse.class).querySingle();
    }

    @Override
    public UsersRecylcerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_item_layout, viewGroup, false);
        return new UsersRecylcerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
       // UserModel userModel = usersList.get(position);
        holder.txtUsername.setText(obj.get(position).display_name);
        holder.txtLocation.setText( (!obj.get(position).region.equals("")?obj.get(position).region+", ":"") + (obj.get(position).country));
        if (obj.get(position).avatar != null && !obj.get(position).avatar.equals(""))
            holder.imgProfile.setImageURI(obj.get(position).avatar);
        else Picasso.with(context).load(R.drawable.default_profile).into(holder.imgProfile);

//        String followerId = obj.get(position).userID;               // Current User we are checking whether he is following US or not
//        String followingId = loginResponse.userID;          // we the logged in user

        // If this user is not logged in user
        if (loginResponse != null && loginResponse.userID.equals(obj.get(position).userID))
        {
            holder.btnFollow.setVisibility(View.VISIBLE);

//            UserModel resultUser = SQLite.select().from(UserModel.class)
//                    .where(UserModel_Table.userID.in(
//                            SQLite.select(RelationshipModel_Table.followingId).from(RelationshipModel.class)
//                                    .where(RelationshipModel_Table.relationshipId.is(followingId + "" + followerId))
//                    )).querySingle();

//            if (resultUser == null) {
//                holder.btnFollow.setLiked(false);
//            } else {
//                holder.btnFollow.setLiked(true);
//                Log.w("RECYCLER", resultUser.display_name);
//            }
        }
        else
        {
            holder.btnFollow.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return obj.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        @BindView(R.id.imgProfile_useritem) public SimpleDraweeView imgProfile;
        @BindView(R.id.txtUsername_useritem) public TextView txtUsername;
        @BindView(R.id.txtLocation_useritem) public TextView txtLocation;
        @BindView(R.id.btnFollow_useritem) public LikeButton btnFollow;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            //btnFollow.setOnLikeListener(this);
        }

        @OnClick(R.id.layoutRoot_useritem)
        public void layoutClick(RelativeLayout layoutRoot)
        {
            Intent intent = new Intent(context, SellerProfileActivity.class);
            intent.putExtra(AppConstants.KEYS.SELLER_ID.name(), obj.get(getAdapterPosition()).userID);
            context.startActivity(intent);
        }

//        @Override
//        public void liked(LikeButton likeButton) {
//
//            // Followed
//            //final LoginResponse login = SQLite.select().from(LoginResponse.class).querySingle();
//
//            Call<VerifyUserResponse> call = NetController.getInstance().getUserService().followUser(
//                    loginResponse.access_token,
//                    AppConstants.VALUES.FOLLOWING,
//                    usersList.get(getAdapterPosition()).userID,
//                    AppConstants.VALUES.STATUS_FOLLOW
//            );
//
//            call.enqueue(new Callback<VerifyUserResponse>() {
//                @Override
//                public void onResponse(Call<VerifyUserResponse> call, Response<VerifyUserResponse> response) {
//                    Log.w("SOLD", "onResponse: Follow Response: " + response.code() );
//
//                    if (response.code() == HTTP_OK)
//                    {
//
//                        UserModel currentUser = usersList.get(getAdapterPosition());
//
//                        SnackUtils.showSnackMessage((Activity)context, "Followed " + currentUser.display_name + "");
//
//                        RelationshipModel relation = new RelationshipModel();
//                        relation.followerId = loginResponse.userID;
//                        relation.followingId = currentUser.userID;
//                        relation.relationshipId = relation.followerId + "" + relation.followingId;
//                        relation.textValue = loginResponse.display_name + " --> " + currentUser.display_name;
//                        relation.save();
//                        //UserModel newUser = new UserModel();
//                        //newUser.cloneUser(currentUser);
//                        //newUser.type = AppConstants.USER_TYPE.FOLLOWING;
//                        //newUser.save();
//
//                        usersFragment.listAdapter.notifyDataSetChanged();
//
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<VerifyUserResponse> call, Throwable t) {
//                    t.printStackTrace();
//                    SnackUtils.showSnackMessage((Activity)context, "Server Error Occured!", false);
//                }
//            });
//
//        }
//
//        @Override
//        public void unLiked(LikeButton likeButton) {
//
//            // Unfollowed
//
//            Call<VerifyUserResponse> call = NetController.getInstance().getUserService().followUser(
//                    loginResponse.access_token,
//                    AppConstants.VALUES.FOLLOWING,
//                    usersList.get(getAdapterPosition()).userID,
//                    AppConstants.VALUES.STATUS_UNFOLLOW
//            );
//
//            call.enqueue(new Callback<VerifyUserResponse>() {
//                @Override
//                public void onResponse(Call<VerifyUserResponse> call, Response<VerifyUserResponse> response) {
//                    Log.w("SOLD", "onResponse: Unfollow Response: " + response.code() );
//
//                    if (response.code() == HTTP_OK)
//                    {
//                        // Updating Local database
//                        UserModel currentUser = usersList.get(getAdapterPosition());
//
//                        SnackUtils.showSnackMessage((Activity)context, "Unfollowed " + currentUser.display_name + "");
//
//                        RelationshipModel model = SQLite.select().from(RelationshipModel.class)
//                                .where(RelationshipModel_Table.followerId.is(loginResponse.userID))
//                                .and(RelationshipModel_Table.followingId.is(currentUser.userID))
//                                .querySingle();
//
//                        if (model != null)
//                        {
//                            model.delete();
//                        }
//
//                        /*
//                        // Get User from following list
//                        UserModel followingUser = SQLite.select().from(UserModel.class).where(UserModel_Table.type.is(AppConstants.USER_TYPE.FOLLOWING)).and(UserModel_Table.userID.is(currentUser.userID)).querySingle();
//                        if (followingUser != null)
//                        {
//                            followingUser.delete();
//                        }
//                        */
//
//                       // usersFragment.refreshLocalData();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<VerifyUserResponse> call, Throwable t) {
//                    t.printStackTrace();
//                    SnackUtils.showSnackMessage((Activity)context, "Server Error Occured!", false);
//                }
//            });
//        }
    }
}
