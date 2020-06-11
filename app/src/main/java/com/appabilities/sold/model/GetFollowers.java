package com.appabilities.sold.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Admin on 11/23/2019.
 */

public class GetFollowers {
    @SerializedName("following_id")
    @Expose
    private String followingId;
    @SerializedName("followers_id")
    @Expose
    private String followersId;
    @SerializedName("following_detail")
    @Expose
    private List<UserModel> followingDetail = null;
    @SerializedName("followers_detail")
    @Expose
    private List<UserModel> followersDetail = null;

    public String getFollowingId() {
        return followingId;
    }

    public void setFollowingId(String followingId) {
        this.followingId = followingId;
    }

    public String getFollowersId() {
        return followersId;
    }

    public void setFollowersId(String followersId) {
        this.followersId = followersId;
    }

    public List<UserModel> getFollowingDetail() {
        return followingDetail;
    }

    public void setFollowingDetail(List<UserModel> followingDetail) {
        this.followingDetail = followingDetail;
    }

    public List<UserModel> getFollowersDetail() {
        return followersDetail;
    }

    public void setFollowersDetail(List<UserModel> followersDetail) {
        this.followersDetail = followersDetail;
    }
}
