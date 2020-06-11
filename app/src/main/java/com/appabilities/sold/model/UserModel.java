package com.appabilities.sold.model;

import com.appabilities.sold.database.SoldDatabase;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.networking.response.SellerProfileResponse;
import com.appabilities.sold.networking.response.SellerResponse;
import com.appabilities.sold.utils.AppConstants;
import com.google.gson.annotations.Expose;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Hamza on 9/13/2017.
 */

@Table(database = SoldDatabase.class)
public class UserModel extends BaseModel {

    @Column @PrimaryKey @Expose public String userID;
    @Column @Expose public String email;
    @Column @Expose public String display_name;
    @Column @Expose public String phone;
    @Column @Expose public String country;
    @Column @Expose public String region;
    @Column @Expose public String address;
    @Column @Expose public String description;
    @Column @Expose public String avatar;
    @Column @Expose public String cover_image;
    @Column @Expose public String total_following;
    @Column @Expose public String total_followers;
    @Column public AppConstants.USER_TYPE type;

    public UserModel(){

    }
    public void saveFollowing(SellerResponse data){
        UserModel userModel = new UserModel();
        userModel.userID = data.userID;
        userModel.email = data.email;
        userModel.display_name = data.displayName;
        userModel.phone = data.phone;
        userModel.country = data.country;
        userModel.region = data.region;
        userModel.address = data.address;
        userModel.description = data.description;
        userModel.avatar = data.avatar;
        userModel.cover_image = data.coverImage;
        userModel.total_followers = data.totalFollowers;
        userModel.total_following = data.totalFollowing;
        userModel.type = AppConstants.USER_TYPE.FOLLOWING;
        userModel.save();
    }


}