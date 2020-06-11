package com.appabilities.sold.database.tables;

import com.appabilities.sold.database.SoldDatabase;
import com.google.gson.annotations.Expose;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * Created by Wajahat Karim on 9/14/2016.
 */

@Table(database = SoldDatabase.class)
public class ProductImageModel extends BaseModel implements Serializable {

    @Column
    @PrimaryKey
    @Expose public String imageID;
    @Column
    @Expose public String img_name;

    @Column
    public String pid;
    /*
    @ForeignKey
    ForeignKeyContainer<ProductModel> productModel;

    public void associateProduct(ProductModel product)
    {
        productModel = FlowManager.getContainerAdapter(ProductModel.class).toForeignKeyContainer(product);
    }
    */

}
