package com.appabilities.sold.networking.response;


import androidx.databinding.BindingAdapter;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Hamza on 9/15/2017.
 */
@Parcel
public class CategoriesResponse {
    @SerializedName("categoryID") @Expose public String categoryID;
    @SerializedName("category_img") @Expose public String categoryImg;
    @SerializedName("category_name") @Expose public String categoryName;
    @SerializedName("subcategories") @Expose public List<SubCategoryResponse> subcategories;
    public int is_selected;


    public CategoriesResponse(){

    }

    public CategoriesResponse(String name){
        categoryName = name;
    }
    @BindingAdapter({"bind:categoryImg"})
    public static void loadCoverImage(SimpleDraweeView imageView, String imageUrl)
    {
        imageView.setImageURI(imageUrl);
    }
}
