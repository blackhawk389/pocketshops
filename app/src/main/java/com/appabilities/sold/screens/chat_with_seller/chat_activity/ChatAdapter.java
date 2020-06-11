package com.appabilities.sold.screens.chat_with_seller.chat_activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appabilities.sold.R;
import com.appabilities.sold.utils.DateUtils;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 8/1/2018.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER = 10;
    private static final int VIEW_TYPE_USER_OTHER = 20;
    private Context context;
    private List<BaseMessage> mMessageList = new ArrayList<>();
    LayoutInflater layoutInflater;
    String my_id;

    public ChatAdapter(Context context, List<BaseMessage> messages, String my_id) {
        this.context = context;
        this.mMessageList = messages;
        this.layoutInflater = LayoutInflater.from(context);
        this.my_id = my_id;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_msg_item_me, parent, false);
            return new UserMessageHolder(view);

        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_msg_item_other, parent, false);
            return new UserMessageHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        UserMessage message = (UserMessage) this.mMessageList.get(position);
        String userId = message.getSender().getUserId();
        if (userId.equals(my_id)) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_USER_OTHER;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserMessage message = (UserMessage) mMessageList.get(position);

        boolean isNewDay = false;

        // If there is at least one item preceding the current one, check the previous message.
        if (position < mMessageList.size() - 1) {
            BaseMessage prevMessage = mMessageList.get(position + 1);

            // If the date of the previous message is different, display the date before the message,
            // and also set isContinuous to false to show information such as the sender's nickname
            // and profile image.
            if (!DateUtils.hasSameDate(message.getCreatedAt(), prevMessage.getCreatedAt())) {
                isNewDay = true;
            }

        } else if (position == mMessageList.size() - 1) {
            isNewDay = true;
        }

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_USER:
                ((UserMessageHolder) holder).bind(context, message, isNewDay, position);
                break;
            case VIEW_TYPE_USER_OTHER:
                ((UserMessageHolder) holder).bind(context, message, isNewDay, position);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (this.mMessageList.size() != 0) {
            return this.mMessageList.size();
        } else {
            return 0;
        }
    }

    private class UserMessageHolder extends RecyclerView.ViewHolder {
        TextView time, date, msg;

        public UserMessageHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.text_group_chat_date);
            msg = itemView.findViewById(R.id.text_group_chat_message);
            time = itemView.findViewById(R.id.text_group_chat_time);
        }

        void bind(Context context, final UserMessage message, boolean isNewDay, final int postion){

            // Show the date if the message was sent on a different date than the previous one.
            if (isNewDay) {
                date.setVisibility(View.VISIBLE);
                date.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                date.setVisibility(View.GONE);
            }

            msg.setText(message.getMessage());
            time.setText(DateUtils.formatTime(message.getCreatedAt()));
        }

    }


    public void addMessage(BaseMessage message) {
        this.mMessageList.add(0, message);
        notifyDataSetChanged();
    }

    public void setMessageList(List<BaseMessage> list){
        this.mMessageList.clear();
        this.mMessageList.addAll(list);
        notifyDataSetChanged();
    }

}
