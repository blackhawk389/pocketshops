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

/**
 * Created by Wajahat on 6/22/2017.
 */

public class LabelFlexAdapter extends BaseRecyclerAdapter<String, LabelFlexAdapter.LabelViewHolder> {

    private int _itemLayoutResID;
    int selectedItemPosition = 0;
    boolean isSelectable = true;

    public LabelFlexAdapter(@LayoutRes int _itemLayoutResID, @Nullable OnItemClickListener mItemClickListener) {
        super(mItemClickListener);
        this._itemLayoutResID = _itemLayoutResID;
    }

    public void setSelectable(boolean selectable) {
        isSelectable = selectable;
    }

    @Override
    public LabelViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        int layoutId = _itemLayoutResID;
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layoutId, parent, false);
        return new LabelViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(LabelViewHolder holder, int position) {
        holder.mBinding.setVariable(BR.labelName, getItem(position));
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

    public void selectItem(String value)
    {
        if (value.equals("rating")){
            selectedItemPosition = 0;
        }else if (value.equals("date")){
            selectedItemPosition = 1;
        }else if (value.equals("name")){
            selectedItemPosition = 2;
        }
        notifyDataSetChanged();
    }

    public String getItemForServer(int position){
        if (position == 0){
            return "rating";
        }else if (position == 1){
            return "date";
        } else if (position == 2) {
            return "name";
        }
        return getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class LabelViewHolder extends BaseRecyclerAdapter.ViewHolder
    {
        FlexLabelItemLayoutBinding bi;

        public LabelViewHolder(ViewDataBinding binding) {
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
