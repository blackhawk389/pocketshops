package com.appabilities.sold.base;


import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wajahat on 4/26/2017.
 */

public abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private OnItemClickListener mItemClickListener;
    private final List<T> _itemsList;

    public BaseRecyclerAdapter(@Nullable OnItemClickListener mItemClickListener) {
        super();
        this.mItemClickListener = mItemClickListener;
        _itemsList = new ArrayList<>();
        setHasStableIds(true);
    }

    @Override
    public abstract long getItemId(int position);

    @Override
    public int getItemCount() {
        return _itemsList.size();
    }

    public T getItem(int position)
    {
        if (_itemsList == null || _itemsList.size() <= 0)
            return null;
        return _itemsList.get(position);
    }

    public void setItems(List<T> items)
    {
        _itemsList.clear();
        _itemsList.addAll(items);
        notifyDataSetChanged();
    }

    public void addItems(T item)
    {
        _itemsList.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(int position)
    {
        if (position < 0 || position >= _itemsList.size()) return;
        _itemsList.remove(position);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener)
    {
        this.mItemClickListener = mItemClickListener;
    }

    public List<T> getItemsList()
    {
        return new ArrayList<>(_itemsList);
    }

    public interface OnItemClickListener<T, VH extends RecyclerView.ViewHolder, VB extends ViewDataBinding> {
        void onItemClick(BaseRecyclerAdapter<T, VH> adapter, VB binding, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ViewDataBinding mBinding;

        public ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && mItemClickListener != null) {
                //noinspection unchecked
                mItemClickListener.onItemClick(BaseRecyclerAdapter.this, mBinding, position);
            }
        }
    }

}
