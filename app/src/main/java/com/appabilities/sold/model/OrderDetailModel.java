package com.appabilities.sold.model;


import org.parceler.Parcel;

/**
 * Created by Hamza on 4/14/2017.
 */

@Parcel
public class OrderDetailModel{

    private String productTitle;
    private String productID;
    private String quantity;
    private String totalItemPrice;
    private String itemPrice;
    private String itemURL;
    private String taxes;
    private String shipping;
    private String status;
    private String orderStatus;
    private String orderReceived;
    private String orderDate;
    private String updatedOn;
    private String orderDID;
    private String isReviewSubmitted;
    private String bidID;

    public String getBidID() {
        return bidID;
    }

    public void setBidID(String bidID) {
        this.bidID = bidID;
    }

    private String order_auction = "0";

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTotalItemPrice() {
        return totalItemPrice;
    }

    public void setTotalItemPrice(String totalItemPrice) {
        this.totalItemPrice = totalItemPrice;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemURL() {
        return itemURL;
    }

    public void setItemURL(String itemURL) {
        this.itemURL = itemURL;
    }

    public String getTaxes() {
        return taxes;
    }

    public void setTaxes(String taxes) {
        this.taxes = taxes;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderReceived() {
        return orderReceived;
    }

    public void setOrderReceived(String orderReceived) {
        this.orderReceived = orderReceived;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getOrderDID() {
        return orderDID;
    }

    public String getOrder_auction() {
        return order_auction;
    }

    public void setOrder_auction(String order_auction) {
        this.order_auction = order_auction;
    }


    public void setOrderDID(String orderDID) {
        this.orderDID = orderDID;
    }
    public String getIsReviewSubmitted() {
        return isReviewSubmitted;
    }

    public void setIsReviewSubmitted(String isReviewSubmitted) {
        this.isReviewSubmitted = isReviewSubmitted;
    }
}
