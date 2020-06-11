package com.appabilities.sold.screens.chat_with_seller.chat_activity;

import com.appabilities.sold.model.ChatMetaData;
import com.appabilities.sold.networking.response.LoginResponse;
import com.appabilities.sold.networking.response.SellerProfileResponse;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;

import java.util.List;

/**
 * Created by Admin on 7/30/2018.
 */

public interface ChatWithSellerContract {

    interface View{
        void setupViews();
        void onLoading();
        void onLoadingFinished(GroupChannel groupChannel, List<BaseMessage> list, ChatMetaData metaData);
        void onLoadingFinishedWithError();
    }

    interface Actions {
        void initScreen();
        void createChatSession(LoginResponse loginResponse, String sellerId, String seller_name, String seller_avatar);
    }
}
