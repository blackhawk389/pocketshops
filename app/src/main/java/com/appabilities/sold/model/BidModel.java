package com.appabilities.sold.model;


import androidx.databinding.BindingAdapter;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by Hamza on 10/25/2017.
 */

public class BidModel {
    public String productTitle;
    public String bidID;
    public String bidPrice;
    public String productImg;
    public String categoryName;
    public String subCategoryName;
    public String ownerName;
    public String description;
    public String aucted;
    public String productID;
    public String productQty;
    public String bidOrdered;

    @BindingAdapter({"bind:productImg"})
    public static void loadCoverImage(SimpleDraweeView imageView, String imageUrl)
    {
        imageView.setImageURI(imageUrl);
    }

}
