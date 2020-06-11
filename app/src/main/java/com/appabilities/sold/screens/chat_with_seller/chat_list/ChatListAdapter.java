package com.appabilities.sold.screens.chat_with_seller.chat_list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.appabilities.sold.R;
import com.appabilities.sold.model.ChatMetaData;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.screens.chat_with_seller.chat_activity.view.ChatWithSellerActivity;
import com.appabilities.sold.utils.AppConstants;
import com.facebook.drawee.view.SimpleDraweeView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 9/26/2018.
 */

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ChatMetaData> metaDataList;
    private LoginResponse loginResponse;
    private ArrayList<String> id = null;
    private boolean markDone;

    public ChatListAdapter(Context context, List<ChatMetaData> metaDataList, LoginResponse loginResponse){
        this.context = context;
        this.metaDataList = metaDataList;
        this.loginResponse = loginResponse;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMetaData data = metaDataList.get(position);
        ((MyViewHolder)holder).bind(data);
    }

    @Override
    public int getItemCount() {
        return metaDataList.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView image;
        TextView name, product_count, time;
        RelativeLayout item;
        View dot;

        public MyViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.user_image);
            name = itemView.findViewById(R.id.seller_name);
            product_count = itemView.findViewById(R.id.product_count);
            time = itemView.findViewById(R.id.msg_time);
            item = itemView.findViewById(R.id.item);
            dot = itemView.findViewById(R.id.red_dot);
        }

        void bind(final ChatMetaData data){
            if(!data.seller_id.equalsIgnoreCase(loginResponse.userID)){
                image.setImageURI(data.seller_avatar);
                name.setText(data.seller_name);
                time.setText(data.last_msg_time);
            }
            else {
                image.setImageURI(data.buyer_avatar);
                name.setText(data.buyer_name);
                time.setText(data.last_msg_time);
            }

            if(id != null && id.contains(data.channel_url)){
                dot.setVisibility(View.VISIBLE);
            }else{
                dot.setVisibility(View.GONE);
            }


            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatWithSellerActivity.class);
                    intent.putExtra(AppConstants.KEYS.CHAT_METADATA.name(), Parcels.wrap(data));
                    if(id != null && id.size() > 0){
                        id.remove(data.channel_url);
                        notifyDataSetChanged();
                    }

                    context.startActivity(intent);
                }
            });
        }
    }

    public void setMetaDataList(List<ChatMetaData> metaDataList){
        this.metaDataList = metaDataList;
        notifyDataSetChanged();
    }


    public void markIncomingMsg(ArrayList<String> ids){
        this.id = ids;
        notifyDataSetChanged();
    }
}
