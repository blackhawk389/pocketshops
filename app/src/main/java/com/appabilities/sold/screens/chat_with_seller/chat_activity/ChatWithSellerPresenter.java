package com.appabilities.sold.screens.chat_with_seller.chat_activity;

import android.util.Log;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.model.ChatMetaData;
import com.appabilities.sold.networking.NetController;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.utils.DateUtils;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.PreviousMessageListQuery;
import com.sendbird.android.SendBirdException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Admin on 7/30/2018.
 */

public class ChatWithSellerPresenter extends BasePresenter<ChatWithSellerContract.View> implements ChatWithSellerContract.Actions {


    public ChatWithSellerPresenter(ChatWithSellerContract.View view) {
        super(view);
    }

    @Override
    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void createChatSession(final LoginResponse loginResponse, final String sellerId, final String seller_name, final String seller_avatar) {
        _view.onLoading();
        List<String> users = new ArrayList<>();
        users.add(loginResponse.userID);
        users.add(sellerId);

        GroupChannel.createChannelWithUserIds(users, true, new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(final GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {
                    _view.onLoadingFinishedWithError();
                    Log.d("tag", "SendBirdException: " + e.getMessage());
                    return;
                }
                Log.d("tag", "url: " + groupChannel.getUrl());

                PreviousMessageListQuery prevMessageListQuery = groupChannel.createPreviousMessageListQuery();
                prevMessageListQuery.load(200, true, new PreviousMessageListQuery.MessageListQueryResult() {
                    @Override
                    public void onResult(final List<BaseMessage> list, SendBirdException e) {
                        if (e != null) {    // Error.
                            _view.onLoadingFinishedWithError();
                            Log.d("tag", "SendBirdException: "+e.getMessage());
                            return;
                        }

                        List<String> keys = new ArrayList<String>();
                        keys.add("seller_id");
                        keys.add("seller_name");
                        keys.add("seller_avatar");
                        keys.add("buyer_id");
                        keys.add("buyer_name");
                        keys.add("buyer_avatar");
                        keys.add("channel_url");

                        groupChannel.getMetaData(keys, new BaseChannel.MetaDataHandler() {
                            @Override
                            public void onResult(Map<String, String> map, SendBirdException e) {
                                if (e != null) {    // Error.
                                    _view.onLoadingFinishedWithError();
                                    Log.d("tag", "SendBirdException: " + e.getMessage());
                                    return;
                                }
                                if(map.size() == 0){
                                    HashMap<String, String> data = new HashMap<String, String>();
                                    data.put("seller_id", sellerId);
                                    data.put("seller_name", seller_name);
                                    data.put("seller_avatar", seller_avatar);
                                    data.put("buyer_id", loginResponse.userID);
                                    data.put("buyer_name", loginResponse.username);
                                    data.put("buyer_avatar", loginResponse.avatar);
                                    data.put("channel_url", groupChannel.getUrl());
                                    groupChannel.createMetaData(data, new BaseChannel.MetaDataHandler() {
                                        @Override
                                        public void onResult(Map<String, String> map, SendBirdException e) {
                                            if (e != null) {    // Error.
                                                _view.onLoadingFinishedWithError();
                                                Log.d("tag", "SendBirdException: "+e.getMessage());
                                                return;
                                            }
                                            _view.onLoadingFinished(groupChannel, list, null);
                                        }
                                    });
                                }
                                else {
                                    _view.onLoadingFinished(groupChannel, list, null);
                                }
                            }
                        });

                    }
                });
            }
        });

    }

    public void loadChatSession(String channel){
        _view.onLoading();
        GroupChannel.getChannel(channel, new GroupChannel.GroupChannelGetHandler() {
            @Override
            public void onResult(final GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {    // Error!
                    return;
                }

                PreviousMessageListQuery prevMessageListQuery = groupChannel.createPreviousMessageListQuery();
                prevMessageListQuery.load(200, true, new PreviousMessageListQuery.MessageListQueryResult() {
                    @Override
                    public void onResult(final List<BaseMessage> list, SendBirdException e) {
                        if (e != null) {    // Error.
                            _view.onLoadingFinishedWithError();
                            Log.d("tag", "SendBirdException: "+e.getMessage());
                            return;
                        }

                        List<String> keys = new ArrayList<String>();
                        keys.add("seller_id");
                        keys.add("seller_name");
                        keys.add("seller_avatar");
                        keys.add("buyer_id");
                        keys.add("buyer_name");
                        keys.add("buyer_avatar");
                        keys.add("channel_url");

                        groupChannel.getMetaData(keys, new BaseChannel.MetaDataHandler() {
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

                                _view.onLoadingFinished(groupChannel, list,data);
                            }
                        });

                    }
                });

            }
        });

    }

    public void sendChatNotification(String access_token, String seller_id, String seller_name, String username, String product_id, String product_title, String channel_url){
        RequestBody body;
        MultipartBody.Builder bodyBuilder;
        bodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("access_token", access_token)
                .addFormDataPart("seller_id", seller_id)
                .addFormDataPart("seller_name", seller_name)
                .addFormDataPart("username", username)
                .addFormDataPart("product_id", product_id)
                .addFormDataPart("product_title", product_title)
                .addFormDataPart("channel_url", channel_url);


        body = bodyBuilder.build();
        Call<BaseResponse> call = NetController.getInstance().getUserService().sendChatFCM(body);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == HTTP_OK){

                }else {

                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.d("tag", "onFailure:"+t.getLocalizedMessage());
            }
        });
    }
}

