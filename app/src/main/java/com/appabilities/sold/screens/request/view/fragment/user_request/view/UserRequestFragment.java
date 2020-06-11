package com.appabilities.sold.screens.request.view.fragment.user_request.view;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseFragment;
import com.appabilities.sold.databinding.FragmentUserRequestBinding;
import com.appabilities.sold.databinding.RequestItemLayoutBinding;
import com.appabilities.sold.networking.response.RequestResponse;
import com.appabilities.sold.screens.add_offer.view.AddOfferActivity;
import com.appabilities.sold.screens.chat_with_seller.chat_activity.view.ChatWithSellerActivity;
import com.appabilities.sold.screens.my_product_detail.view.MyProductDetailActivity;
import com.appabilities.sold.screens.request.view.RequestActivity;
import com.appabilities.sold.screens.request.view.fragment.user_request.UserRequestContract;
import com.appabilities.sold.screens.request.view.fragment.user_request.UserRequestPresenter;
import com.appabilities.sold.screens.request_detail.view.RequestDetailActivity;
import com.appabilities.sold.screens.view_offer.view.ViewOfferActivity;
import com.appabilities.sold.utils.AppConstants;
import com.appabilities.sold.utils.SnackUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kennyc.view.MultiStateView;
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserRequestFragment extends BaseFragment implements UserRequestContract.View<List<RequestResponse>>,
        Function4<View, RequestResponse, Integer, Map<Integer, ? extends View>, Unit> {

    UserRequestPresenter presenter;
    FragmentUserRequestBinding bi;
    RecyclerAdapterUtil<RequestResponse> mUserRequestAdapter;
    LinearLayoutManager layoutManager;

    public UserRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bi = DataBindingUtil.inflate(inflater, R.layout.fragment_user_request, container, false);
        bi.setView(this);
        presenter = new UserRequestPresenter(this);
        presenter.initScreen();
        bi.setPresenter(presenter);
        return bi.getRoot();
    }

    @Override
    public void setupViews() {
        mUserRequestAdapter = new RecyclerAdapterUtil<>(mActivity, new ArrayList<RequestResponse>(),R.layout.request_item_layout);
        mUserRequestAdapter.addOnDataBindListener(this);
        bi.recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        bi.recyclerView.setLayoutManager(layoutManager);
        bi.recyclerView.setAdapter(mUserRequestAdapter);

    }

    @Override
    public void onLoading() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    @Override
    public void onRequestSuccessful(List<RequestResponse> responseData) {
        mUserRequestAdapter.getItemList().clear();
        mUserRequestAdapter.getItemList().addAll(responseData);
        mUserRequestAdapter.notifyDataSetChanged();
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
    }

    @Override
    public void onRequestFail(String errorMessage) {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void onRequestNotFound() {
        bi.multiSateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
    }

    @Override
    public Unit invoke(View view, final RequestResponse requestResponse, final Integer integer, Map<Integer, ? extends View> integerMap) {
        RequestItemLayoutBinding binding = DataBindingUtil.bind(view);
        binding.setRequestItem(requestResponse);
        binding.executePendingBindings();
        if (requestResponse.subcategoryName != null)
            binding.txtCategory.setText(requestResponse.categoryName +"->"+requestResponse.subcategoryName);
        else
            binding.txtCategory.setText(requestResponse.categoryName);
        binding.txtTime.setText(AppConstants.dateDiff(requestResponse.updatedOn));
        binding.layoutRequest.setVisibility(View.VISIBLE);
        binding.layoutMyRequest.setVisibility(View.GONE);
        binding.btnMakeOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ViewOfferActivity.class);
                intent.putExtra(AppConstants.KEYS.PRODUCT_ID.name(), requestResponse.productID);
                startActivity(intent);

            }
        });
        binding.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatWithSellerActivity.class);
                intent.putExtra(AppConstants.KEYS.REQUEST_ITEM.name(), Parcels.wrap(requestResponse));
                startActivity(intent);
            }
        });

        binding.layoutRootBiditem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = layoutManager.findViewByPosition(integer);
                SimpleDraweeView imgProduct = (SimpleDraweeView) view.findViewById(R.id.img_product);
                Intent intent = new Intent(mActivity, RequestDetailActivity.class);
                intent.putExtra(AppConstants.KEYS.REQUEST_ITEM.name(), Parcels.wrap(requestResponse));
                intent.putExtra(AppConstants.KEYS.IS_FROM_MY_REQUEST.name(), false);
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    intent.putExtra(AppConstants.KEYS.TRANSITION_VIEW.name(), ViewCompat.getTransitionName(imgProduct));
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            mActivity,
                            imgProduct,
                            ViewCompat.getTransitionName(imgProduct));

                    startActivity(intent, options.toBundle());
                }else {
                    startActivity(intent);
                }

            }
        });
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.getRequest(((RequestActivity)mActivity).loginResponse.access_token);
    }


}
