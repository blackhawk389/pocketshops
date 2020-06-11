package com.appabilities.sold.screens.chat_with_seller.chat_list;

import android.util.Log;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.model.ChatMetaData;
import com.appabilities.sold.utils.DateUtils;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.SendBirdException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 9/26/2018.
 */

public class ChatListPresenter extends BasePresenter<ChatListContract.View> implements ChatListContract.Actions {

    protected ChatListPresenter(ChatListContract.View view) {
        super(view);
    }

    @Override
    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void getChatMetadata() {
        _view.onLoading();
        GroupChannelListQuery channelListQuery = GroupChannel.createMyGroupChannelListQuery();
        channelListQuery.setIncludeEmpty(true);
        channelListQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
            @Override
            public void onResult(final List<GroupChannel> list, SendBirdException e) {
                if (e != null) {    // Error.
                    _view.onLoadingFinishedWithError();
                    Log.d("tag", "SendBirdException: " + e.getMessage());
                    return;
                }

                final List<ChatMetaData> metaDataList = new ArrayList<>();
                if(list.size() != 0){
                    for (int i = 0; i < list.size() ; i++) {
                        List<String> keys = new ArrayList<String>();
                        keys.add("seller_id");
                        keys.add("seller_name");
                        keys.add("seller_avatar");
                        keys.add("buyer_id");
                        keys.add("buyer_name");
                        keys.add("buyer_avatar");
                        keys.add("channel_url");

                        final int finalI = i;
                        list.get(i).getMetaData(keys, new BaseChannel.MetaDataHandler() {
                            @Override
                            public void onResult(Map<String, String> map, SendBirdException e) {
                                if (e != null) {    // Error.
                                    _view.onLoadingFinishedWithError();
                                    Log.d("tag", "SendBirdException: " + e.getMessage());
                                    return;
                                }
                                ChatMetaData data = new ChatMetaData();
                                data.seller_id = map.get("seller_id");
                                data.seller_name = map.get("seller_name");
                                data.seller_avatar = map.get("seller_avatar");
                                data.buyer_id = map.get("buyer_id");
                                data.buyer_name = map.get("buyer_name");
                                data.buyer_avatar = map.get("buyer_avatar");
                                data.channel_url = map.get("channel_url");

                                if( list.get(finalI).getLastMessage() != null){
                                    data.last_msg_time = DateUtils.formatTime(list.get(finalI).getLastMessage().getCreatedAt());
                                }
                                metaDataList.add(data);
                                if(finalI == list.size()-1){
                                    _view.onChatMetadata(metaDataList);
                                }
                            }
                        });
                    }
                }
                else {
                    _view.onLoadingFinishedWithError();
                }
            }
        });
    }
}
