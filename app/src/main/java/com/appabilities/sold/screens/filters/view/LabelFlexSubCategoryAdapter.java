package com.appabilities.sold.screens.filters.view;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.appabilities.sold.BR;
import com.appabilities.sold.base.BaseRecyclerAdapter;
import com.appabilities.sold.databinding.FlexLabelItemLayoutBinding;
import com.appabilities.sold.networking.response.SubCategoryResponse;

/**
 * Created by Hamza on 10/5/2017.
 */

public class LabelFlexSubCategoryAdapter extends BaseRecyclerAdapter<SubCategoryResponse, LabelFlexSubCategoryAdapter.ViewHolder> {

    private int _layoutID;
    int selectedItemPosition = 0;
    boolean isSelectable = true;

    public LabelFlexSubCategoryAdapter(@LayoutRes int _layoutID, @Nullable OnItemClickListener mItemClickListener) {
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
        holder.mBinding.setVariable(BR.labelName, getItem(position).categoryName);
        holder.mBinding.executePendingBindings();

        if (isSelectable)
        {
            if (position == selectedItemPosition)
                holder.selectItem();
            else holder.deselectItem();
        }
    }

    public void selectItem(int position)
    {
        selectedItemPosition = position;
        notifyDataSetChanged();
    }

    public void selectItem(SubCategoryResponse value)
    {
        for (int i=0; i<getItemsList().size(); i++)
        {
            if (value.categoryName.equals(getItem(i).categoryName))
            {
                selectItem(i);
                return;
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewHolder extends BaseRecyclerAdapter.ViewHolder {
        FlexLabelItemLayoutBinding bi;

        public ViewHolder(ViewDataBinding binding) {
            super(binding);
            bi = (FlexLabelItemLayoutBinding) binding;
        }

        public void selectItem()
        {
            bi.txtLabelLabelitem.setChecked(true);
            bi.txtLabelLabelitem.setTextColor(Color.WHITE);
        }

        public void deselectItem()
        {
            bi.txtLabelLabelitem.setChecked(false);
            bi.txtLabelLabelitem.setTextColor(Color.parseColor("#464646"));
        }
    }
}