package com.appabilities.sold.networking.response;

import com.appabilities.sold.database.SoldDatabase;
import com.appabilities.sold.database.tables.RelationshipModel;
import com.appabilities.sold.database.tables.RelationshipModel_Table;
import com.appabilities.sold.model.UserModel;
import com.appabilities.sold.model.UserModel_Table;
import com.google.gson.annotations.Expose;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * Created by Hamza on 9/13/2017.
 */

@Table(database = SoldDatabase.class)
public class LoginResponse extends BaseModel {



    @Column
    @PrimaryKey
    @Expose
    public int localID;
    @Column @PrimaryKey @Expose public String userID;
    @Column @Expose public String username;
    @Column @Expose public String email;
    @Column @Expose public String display_name;
    @Column @Expose public String avatar;
    @Column @Expose public String phone;
    @Column @Expose public String country;
    @Column @Expose public String region;
    @Column @Expose public String address;
    @Column @Expose public String access_token;
    @Column @Expose public String cover_image;
    @Column @Expose public String selected_category = "";
    @Column @Expose public String total_following;
    @Column @Expose public String total_followers;
    @Column @Expose public String following_id;
    @Column @Expose public String followers_id;
    @Column @Expose public String description;


    public int getLocalID() {
        return localID;
    }

    public void setLocalID(int localID) {
        this.localID = localID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getSelected_category() {
        return selected_category;
    }

    public void setSelected_category(String selected_category) {
        this.selected_category = selected_category;
    }

    public String getTotal_following() {
        return total_following;
    }

    public void setTotal_following(String total_following) {
        this.total_following = total_following;
    }

    public String getTotal_followers() {
        return total_followers;
    }

    public void setTotal_followers(String total_followers) {
        this.total_followers = total_followers;
    }

    public String getFollowing_id() {
        return following_id;
    }

    public void setFollowing_id(String following_id) {
        this.following_id = following_id;
    }

    public String getFollowers_id() {
        return followers_id;
    }

    public void setFollowers_id(String followers_id) {
        this.followers_id = followers_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UserModel> getFollowers_detail() {
        return followers_detail;
    }

    public void setFollowers_detail(List<UserModel> followers_detail) {
        this.followers_detail = followers_detail;
    }

    public List<UserModel> getFollowing_detail() {
        return following_detail;
    }

    public void setFollowing_detail(List<UserModel> following_detail) {
        this.following_detail = following_detail;
    }

    // Followers
    @Expose public List<UserModel> followers_detail;
    @OneToMany(methods = OneToMany.Method.ALL, variableName = "followers_detail")
    public List<UserModel> getFollowersList()
    {
        if (followers_detail == null || followers_detail.isEmpty())
        {
            //followers_detail = SQLite.select().from(UserModel.class).where(UserModel_Table.type.is(AppConstants.USER_TYPE.FOLLOWER)).queryList();
            followers_detail = SQLite.select().from(UserModel.class).where(UserModel_Table.userID.in(new Select(RelationshipModel_Table.followerId).from(RelationshipModel.class).where(RelationshipModel_Table.followingId.is(userID)))).queryList();
        }
        return followers_detail;
    }

    // Following
    @Expose public List<UserModel> following_detail;
    @OneToMany(methods = OneToMany.Method.ALL, variableName = "following_detail")
    public List<UserModel> getFollowingsList()
    {
        if (following_detail == null || following_detail.isEmpty())
        {
            //following_detail = SQLite.select().from(UserModel.class).where(UserModel_Table.type.is(AppConstants.USER_TYPE.FOLLOWING)).queryList();
            following_detail = SQLite.select().from(UserModel.class).where(UserModel_Table.userID.in(new Select(RelationshipModel_Table.followingId).from(RelationshipModel.class).where(RelationshipModel_Table.followerId.is(userID)))).queryList();
        }
        return following_detail;
    }

}
