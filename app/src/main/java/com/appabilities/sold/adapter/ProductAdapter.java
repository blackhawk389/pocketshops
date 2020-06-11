package com.appabilities.sold.adapter;


import android.view.LayoutInflater;
import android.view.ViewGroup;


import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.appabilities.sold.BR;
import com.appabilities.sold.base.BaseRecyclerAdapter;
import com.appabilities.sold.networking.response.ProductResponse;

/**
 * Created by Hamza on 9/27/2017.
 */

public class ProductAdapter extends BaseRecyclerAdapter<ProductResponse, ProductAdapter.ViewHolder> {

    @LayoutRes
    private int _layoutID;

    public ProductAdapter(@LayoutRes int _layoutID, @Nullable OnItemClickListener mItemClickListener) {
        super(mItemClickListener);
        this._layoutID = _layoutID;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), _layoutID, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mBinding.setVariable(BR.productModel, getItem(position));
        holder.mBinding.executePendingBindings();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewHolder extends BaseRecyclerAdapter.ViewHolder {
        public ViewHolder(ViewDataBinding binding) {
            super(binding);
        }
    }
}
