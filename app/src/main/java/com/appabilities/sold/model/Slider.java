package com.appabilities.sold.model;

/**
 * Created by Hamza on 4/25/2017.
 */

public class Slider {
    private String catId;
    private String slideText;
    private String slideImg;

    public Slider(String catId, String slideText, String slideImg) {
        this.catId = catId;
        this.slideText = slideText;
        this.slideImg = slideImg;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getSlideText() {
        return slideText;
    }

    public void setSlideText(String slideText) {
        this.slideText = slideText;
    }

    public String getSlideImg() {
        return slideImg;
    }

    public void setSlideImg(String slideImg) {
        this.slideImg = slideImg;
    }

}
