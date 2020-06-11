package com.appabilities.sold.screens.chat_with_seller.chat_list;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.sidemenu.SideMenuBaseActivity;
import com.appabilities.sold.databinding.ActivityChatListBinding;
import com.appabilities.sold.model.ChatMetaData;
import com.appabilities.sold.utils.CommonActions;
import com.kennyc.view.MultiStateView;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.SendBird;
import com.sendbird.android.UserMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends SideMenuBaseActivity implements ChatListContract.View {
    ActivityChatListBinding binding;
    ChatListPresenter presenter;
    ChatListAdapter chatListAdapter;
    ArrayList<String> chats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = (ActivityChatListBinding) setContentLayout(R.layout.activity_chat_list);
        binding.setView(this);
        CommonActions.hideKeyboard(this);
        presenter = new ChatListPresenter(this);
        binding.setPresenter(presenter);
//        initDrawer(binding.toolbar, "Chat");
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        changeStatusBarColor();
        //setSupportActionBar(binding.toolbar);
        //getSupportActionBar().setTitle("Chat");
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(false);
        binding.recyclerViewChatlist.setLayoutManager(mLayoutManager);
        chatListAdapter = new ChatListAdapter(this, new ArrayList<ChatMetaData>(), loginResponse);
        binding.recyclerViewChatlist.setAdapter(chatListAdapter);
        presenter.getChatMetadata();

        SendBird.addChannelHandler(loginResponse.userID, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                if (baseMessage instanceof UserMessage) {
                    chats.add(baseMessage.getChannelUrl());
                    chatListAdapter.markIncomingMsg(chats);
                }
            }
        });
    }

    @Override
    public void onLoading() {
//        binding.progress.setVisibility(View.VISIBLE);
//        binding.container.setVisibility(View.GONE);
        binding.multiSateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        binding.errorMsg.setVisibility(View.GONE);
    }

    @Override
    public void onChatMetadata(List<ChatMetaData> metaDataList) {
//        binding.progress.setVisibility(View.GONE);
//        binding.errorMsg.setVisibility(View.GONE);
//        binding.container.setVisibility(View.VISIBLE);
        binding.multiSateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        initDrawer(binding.toolbar, "Chat (" + metaDataList.size()+ ")");
        chatListAdapter.setMetaDataList(metaDataList);
    }

    @Override
    public void onLoadingFinishedWithError() {
        binding.multiSateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
//        binding.progress.setVisibility(View.GONE);
//        binding.container.setVisibility(View.GONE);
//        binding.errorMsg.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
     //   presenter.getChatMetadata();
        selectMenuOption(R.id.nav_chat);
    }
}
