package com.appabilities.sold.screens.chat_with_seller.chat_activity.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.databinding.ActivityChatWithSellerBinding;
import com.appabilities.sold.model.ChatMetaData;
import com.appabilities.sold.networking.response.BidItemResponse;
import com.appabilities.sold.networking.response.OfferResponse;
import com.appabilities.sold.networking.response.ProductResponse;
import com.appabilities.sold.networking.response.RequestResponse;
import com.appabilities.sold.networking.response.SellerProfileResponse;
import com.appabilities.sold.screens.chat_with_seller.chat_activity.ChatAdapter;
import com.appabilities.sold.screens.chat_with_seller.chat_activity.ChatWithSellerContract;
import com.appabilities.sold.screens.chat_with_seller.chat_activity.ChatWithSellerPresenter;
import com.appabilities.sold.utils.AppConstants;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserMessage;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ChatWithSellerActivity extends BaseActivity implements ChatWithSellerContract.View {

    ActivityChatWithSellerBinding bi;
    ChatWithSellerPresenter presenter;
    List<BaseMessage> mMessageList = new ArrayList<>();
    ChatAdapter chatAdapter;
    String sellerID = "";
    SellerProfileResponse mSellerResponse;
    ProductResponse productResponse;
    RequestResponse requestResponse;
    OfferResponse offerResponse;
    Bundle bundle;
    String channel_url="", seller_id="", seller_name="", username="", product_id="", product_title = "";
    ChatMetaData chatMetaData;
    BidItemResponse bidder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_chat_with_seller);
        bi.setView(this);
        presenter = new ChatWithSellerPresenter(this);
        bi.setPresenter(presenter);
        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        changeStatusBarColor();
        setSupportActionBar(bi.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSellerResponse = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.SELLER_ID.name()));
        productResponse = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.PRODUCT_ITEM.name()));
        requestResponse = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.REQUEST_ITEM.name()));
        bidder = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.BIDDER.name()));
        offerResponse = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.OFFER_ITEM.name()));
        chatMetaData = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.KEYS.CHAT_METADATA.name()));
        bundle = getIntent().getBundleExtra("chatBundle");

        if(mSellerResponse != null){
            //String userName=mSellerResponse.username;
            getSupportActionBar().setTitle(mSellerResponse.username);
            presenter.createChatSession(loginResponse, mSellerResponse.userID, mSellerResponse.username, mSellerResponse.avatar);
        }
        else if(productResponse != null) {
            getSupportActionBar().setTitle(productResponse.productOwner+" > "+productResponse.productTitle);
            presenter.createChatSession(loginResponse, productResponse.userID, productResponse.productOwner, productResponse.productOwnerAvatar);
        }
        else if(bidder != null) {
            getSupportActionBar().setTitle(bidder.display_name);
            presenter.createChatSession(loginResponse, bidder.bidder_id, bidder.display_name, bidder.avatar);
        } else if(requestResponse != null) {
            getSupportActionBar().setTitle(requestResponse.requesterName);
            presenter.createChatSession(loginResponse, requestResponse.requesterID, requestResponse.requesterName, requestResponse.requesterAvatar);
        }
        else if(offerResponse != null) {
            getSupportActionBar().setTitle(offerResponse.sellerName);
            presenter.createChatSession(loginResponse, offerResponse.userID, offerResponse.sellerName, offerResponse.seller_avatar);
        }
        else if(chatMetaData != null) {
            if(chatMetaData.buyer_id.equalsIgnoreCase(loginResponse.userID)){
                getSupportActionBar().setTitle(chatMetaData.seller_name);
                presenter.createChatSession(loginResponse, chatMetaData.seller_id, chatMetaData.seller_name, chatMetaData.seller_avatar);
            } else {
                getSupportActionBar().setTitle(chatMetaData.buyer_name);
                presenter.createChatSession(loginResponse, chatMetaData.buyer_id, chatMetaData.buyer_id, chatMetaData.buyer_id);
            }
        }
        else if(bundle != null){
            presenter.loadChatSession(channel_url);
            channel_url = bundle.getString("channel_url");
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        bi.recyclerViewChat.setLayoutManager(mLayoutManager);
        chatAdapter = new ChatAdapter(this, mMessageList, loginResponse.userID);
        bi.recyclerViewChat.setAdapter(chatAdapter);
        Log.d("tag", "loginresponse: "+loginResponse.userID+" email:"+loginResponse.email+" username:"+loginResponse.username+" displayname:"+loginResponse.display_name);
    }

    @Override
    public void onLoading() {
        bi.progress.setVisibility(View.VISIBLE);
        bi.container.setVisibility(View.GONE);
        bi.errorMsg.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingFinished(final GroupChannel groupChannel, List<BaseMessage> list, ChatMetaData metaData) {
        bi.progress.setVisibility(View.GONE);
        bi.errorMsg.setVisibility(View.GONE);
        bi.container.setVisibility(View.VISIBLE);
        chatAdapter.setMessageList(list);
        SendBird.addChannelHandler(loginResponse.userID, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                if (baseMessage instanceof UserMessage) {
                    // message is a UserMessage
                    if (baseChannel.getUrl().equals(groupChannel.getUrl())){
                        chatAdapter.addMessage(baseMessage);
                    }
                }
            }
        });

        channel_url = groupChannel.getUrl();
        username = loginResponse.display_name;
        if(mSellerResponse != null){
            seller_id = mSellerResponse.userID;
            seller_name = mSellerResponse.displayName;
        }
        if(requestResponse != null){
            seller_id = requestResponse.requesterID;
            seller_name = requestResponse.requesterName;
        }
        if(offerResponse != null){
            seller_id = offerResponse.userID;
            seller_name = offerResponse.sellerName;
        }
        if(productResponse != null){
            seller_id = productResponse.userID;
            product_id = productResponse.productID;
            product_title = productResponse.productTitle;
        }
        if(chatMetaData != null){
            seller_id = chatMetaData.seller_id;
            if(!seller_id.equalsIgnoreCase(loginResponse.userID)){
                seller_name = chatMetaData.seller_name;
            }
            else {
                seller_id = chatMetaData.buyer_id;
                seller_name = chatMetaData.buyer_name;
            }
            /*if(!seller_id.equalsIgnoreCase(loginResponse.userID)){
                seller_id = chatMetaData.seller_id;
                seller_name = chatMetaData.seller_name;
            }
            else {
                seller_id = loginResponse.userID;
                seller_name = loginResponse.display_name;
            }*/
        }

        if(bundle != null){
            chatMetaData = metaData;
            seller_id = chatMetaData.seller_id;
            if(!seller_id.equalsIgnoreCase(loginResponse.userID)){
                seller_name = chatMetaData.seller_name;
            }
            else {
                seller_id = chatMetaData.buyer_id;
                seller_name = chatMetaData.buyer_name;
            }
            /*if(bundle.getString("seller_id") != null){
                seller_id = bundle.getString("seller_id");
                if(groupChannel.getMembers().get(0).getUserId().equalsIgnoreCase(seller_id)){
                    seller_id = groupChannel.getMembers().get(1).getUserId();
                }
                seller_name = bundle.getString("seller_name");
            }
            else if(TextUtils.isEmpty(bundle.getString("product_id"))){
                seller_id = bundle.getString("product_id");
                product_id = bundle.getString("product_id");
                seller_name = bundle.getString("seller_name");
                product_title = bundle.getString("product_title");
            }*/
        }

        bi.buttonChatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = bi.edittextChatMessage.getText().toString();
                if(msg.length() > 0){
                    groupChannel.sendUserMessage(msg, new BaseChannel.SendUserMessageHandler() {
                        @Override
                        public void onSent(UserMessage userMessage, SendBirdException e) {
                            if (e != null) {    // Error.
                                Log.d("tag", "SendBirdException: "+e.getMessage());
                                return;
                            }
                            bi.edittextChatMessage.setText("");
                            chatAdapter.addMessage(userMessage);
                            presenter.sendChatNotification(loginResponse.access_token, seller_id, seller_name, username, product_id, product_title, channel_url);
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onLoadingFinishedWithError() {
        bi.progress.setVisibility(View.GONE);
        bi.container.setVisibility(View.GONE);
        bi.errorMsg.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SendBird.removeChannelHandler(loginResponse.userID);
    }

}
