package com.appabilities.sold.model;

import org.parceler.Parcel;

/**
 * Created by Hamza on 9/21/2017.
 */

@Parcel
public class SearchModel {
    public String searchText = "";
    public Number minRange = 0;
    public Number maxRange = 1000;
    public int categoryID = -1;
    public int subCategoryID = -1;
    public String sortType = "";
    public String searchSellerName = "";

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Number getMinRange() {
        return minRange;
    }

    public void setMinRange(Number minRange) {
        this.minRange = minRange;
    }

    public Number getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(Number maxRange) {
        this.maxRange = maxRange;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public int getSubCategoryID() {
        return subCategoryID;
    }

    public void setSubCategoryID(int subCategoryID) {
        this.subCategoryID = subCategoryID;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getSearchSellerName() {
        return searchSellerName;
    }

    @Override
    public String toString() {
        return "SearchModel{" +
                "searchText='" + searchText + '\'' +
                ", minRange=" + minRange +
                ", maxRange=" + maxRange +
                ", categoryID=" + categoryID +
                ", subCategoryID=" + subCategoryID +
                ", sortType='" + sortType + '\'' +
                ", searchSellerName='" + searchSellerName + '\'' +
                '}';
    }

    public void setSearchSellerName(String searchSellerName) {
        this.searchSellerName = searchSellerName;
    }
}
