package com.appabilities.sold.screens.chat_with_seller.chat_list;

import com.appabilities.sold.model.ChatMetaData;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;

import java.util.List;

/**
 * Created by Admin on 9/26/2018.
 */

public interface ChatListContract {

    interface View{
        void setupViews();
        void onLoading();
        void onChatMetadata(List<ChatMetaData> metaDataList);
        void onLoadingFinishedWithError();
    }

    interface Actions {
        void initScreen();
        void getChatMetadata();
    }
}
