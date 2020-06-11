package com.appabilities.sold.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appabilities.sold.R;
import com.appabilities.sold.networking.response.AdvertisementResponse;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import java.util.List;

/**
 * Created by Hamza on 4/25/2017.
 */

public class FeatureImagePagerAdapter extends LoopPagerAdapter {

    List<AdvertisementResponse> listAd;
    RollPagerView rollPagerView;
    Activity activity;
    boolean isFailed;

    public FeatureImagePagerAdapter(RollPagerView rollPagerView, Activity activity, List<AdvertisementResponse> listAdvertisement, boolean isFailed) {
        super(rollPagerView);
        this.rollPagerView = rollPagerView;
        this.listAd = listAdvertisement;
        this.activity = activity;
        this.isFailed = isFailed;
        hideOrVisibleIndicators();
    }


    @Override
    public View getView(ViewGroup container, int position) {

        SimpleDraweeView view = new SimpleDraweeView(container.getContext());
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        if(!isFailed) {
            AdvertisementResponse slider = listAd.get(position);
            view.setImageURI(slider.advertImg);
        }else{
            view.setImageResource(R.drawable.place_holder);
        }
       /* Glide.with(activity)
                .load(slider.getSlideImg())
                .into(view);*/
        return view;

    }

    @Override
    public int getRealCount() {
        return listAd == null ? 1 : listAd.size();
    }

    public void refreshList(List<AdvertisementResponse> sliderArrayList) {
        if(listAd != null) {
            this.listAd.clear();
            this.listAd.addAll(sliderArrayList);
        }
        notifyDataSetChanged();
        hideOrVisibleIndicators();
    }

    public void hideOrVisibleIndicators() {

        if (getRealCount() > 0) {

            if (getRealCount() == 1) {
                rollPagerView.setHintView(null);
            } else {
                rollPagerView.setHintView(new ColorPointHintView(activity.getApplicationContext(),activity.getResources().getColor(android.R.color.transparent), android.R.color.transparent));
            }
        }
    }

    public AdvertisementResponse getObject(int position) {
        return listAd.get(position);
    }

}