package com.appabilities.sold.database.tables;


import androidx.databinding.BindingAdapter;

import com.appabilities.sold.database.SoldDatabase;
import com.appabilities.sold.networking.response.ProductResponse;
import com.facebook.drawee.view.SimpleDraweeView;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Hamza on 10/12/2017.
 */
@Table(database = SoldDatabase.class)
public class CartItemModel extends BaseModel {
    @Column @PrimaryKey(autoincrement = true) public int itemId;
    @Column public String productId;
    @Column public String title;
    @Column public String seller;
    @Column public int quantity;
    @Column public int maxQuantity;
    @Column public String price;
    @Column public boolean isIncluded;
    @Column public String imgUrl;

    public CartItemModel() {
        super();
    }

    public CartItemModel(ProductResponse productModel, int q) {
        super();

        // Adding Item in Cart DB
        productId = productModel.productID;
        title = productModel.productTitle;
        seller = productModel.productOwner;
        price = productModel.price;
        quantity = q;
        imgUrl = productModel.imgName;
        maxQuantity = Integer.parseInt(productModel.quantity);
        isIncluded = true;
    }

    @BindingAdapter({"bind:imgUrl"})
    public static void loadCoverImage(SimpleDraweeView imageView, String imageUrl)
    {
        imageView.setImageURI(imageUrl);
    }
}
