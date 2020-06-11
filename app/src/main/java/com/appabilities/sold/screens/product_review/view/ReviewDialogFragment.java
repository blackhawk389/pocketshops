package com.appabilities.sold.screens.product_review.view;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.appabilities.sold.R;
import com.appabilities.sold.databinding.FragmentReviewDialogBinding;
import com.appabilities.sold.networking.response.OrderResponseDetail;
import com.appabilities.sold.screens.my_order_detail.view.MyOrderDetailActivity;
import com.appabilities.sold.screens.product_review.ReviewContract;
import com.appabilities.sold.screens.product_review.ReviewPresenter;
import com.appabilities.sold.utils.SnackUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewDialogFragment extends DialogFragment implements ReviewContract.View{

    static OrderResponseDetail orderDetail;
    FragmentReviewDialogBinding bi;
    ReviewPresenter presenter;
    static String accessToken;
    static MyOrderDetailActivity activity;

    public static ReviewDialogFragment newInstance(OrderResponseDetail orderDetailModel, String access_token, MyOrderDetailActivity activity1) {
        orderDetail = orderDetailModel;
        accessToken = access_token;
       activity = activity1;
        return new ReviewDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate view
        View view = inflater.inflate(R.layout.fragment_review_dialog, container);
        bi = DataBindingUtil.bind(view);
        bi.setView(this);
        presenter = new ReviewPresenter(this);
        presenter.initScreen();
        bi.setPresenter(presenter);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return bi.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //ratingBar.setStepSize(1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void setupViews() {
        bi.ratingBar.setRating(0);
    }

    @Override
    public void onClickSubmitReview() {
        if(bi.ratingBar.getRating() > 0.0) {
            bi.btnSubmitReview.startAnimation();
            presenter.submitReview(accessToken,
                    orderDetail.productID,
                    String.valueOf(bi.ratingBar.getRating()),
                    orderDetail.orderDID,
                    bi.txtComments.getText().toString().trim());
        }else{
            Toast.makeText(getActivity(), "Please select rating", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showSuccessfullReviewSubmit() {
        bi.btnSubmitReview.revertAnimation();
        SnackUtils.showSnackMessage(getActivity(),"Review Submit Successfully");
        activity.hideButton();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getDialog().dismiss();

            }
        }, 500);
    }

    @Override
    public void showErrorSubmitReview() {
        SnackUtils.showSnackMessage(getActivity(),"Error in sending review");
    }
}
