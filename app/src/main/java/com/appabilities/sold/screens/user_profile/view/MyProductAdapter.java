package com.appabilities.sold.screens.user_profile.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.appabilities.sold.R;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.screens.my_product_detail.view.MyProductDetailActivity;
import com.appabilities.sold.utils.AppConstants;
import com.like.LikeButton;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Hamza on 6/30/2017.
 */

public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.MyViewHolder> {

    private Context mContext;
    public List<ProductResponse> productList;
    LoginResponse loginResponse;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgProduct_productitem)
        ImageView imgProductProductitem;
        @BindView(R.id.txtProductName_productitem)
        TextView txtProductNameProductitem;
        @BindView(R.id.txtProduct_sellername)
        TextView txtProductSellername;
        @BindView(R.id.txtProduct_productrating)
        TextView txtProductProductrating;
        @BindView(R.id.likeButton_productitem)
        LikeButton likeButtonProductitem;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    public MyProductAdapter(Context mContext, List<ProductResponse> productList) {
        this.mContext = mContext;
        this.productList = productList;
        loginResponse = SQLite.select().from(LoginResponse.class).querySingle();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ProductResponse product = productList.get(position);

        if (product.imgName != null && !product.imgName.equals("")){
            Picasso.with(mContext)
                    .load(product.imgName)
                    .into(holder.imgProductProductitem);
        }
        holder.txtProductNameProductitem.setText(product.productTitle);
        holder.txtProductSellername.setText(product.productOwner);
        holder.txtProductProductrating.setText(product.ratings);
        if (loginResponse.userID.equals(product.userID)){
            holder.likeButtonProductitem.setVisibility(View.GONE);
        }
        if (product.liked != null){
            if (product.liked.equals("0")){
                holder.likeButtonProductitem.setEnabled(false);
            }else {
                holder.likeButtonProductitem.setEnabled(true);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(mContext, MyProductDetailActivity.class);
                ii.putExtra(AppConstants.KEYS.PRODUCT_ITEM.name(), Parcels.wrap(product));
                mContext.startActivity(ii);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}