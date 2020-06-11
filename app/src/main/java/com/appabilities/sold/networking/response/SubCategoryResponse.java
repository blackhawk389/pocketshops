package com.appabilities.sold.networking.response;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BindingAdapter;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hamza on 4/22/2017.
 */

@org.parceler.Parcel
public class SubCategoryResponse {
    @SerializedName("categoryID") @Expose public String categoryID;
    @SerializedName("category_img") @Expose public String categoryImg;
    @SerializedName("category_name") @Expose public String categoryName;

    public SubCategoryResponse(){

    }

    public SubCategoryResponse(String name){
        categoryName = name;
    }

    @BindingAdapter({"bind:categoryImg"})
    public static void loadCoverImage(SimpleDraweeView imageView, String imageUrl)
    {
        imageView.setImageURI(imageUrl);
    }
}
