package com.appabilities.sold.screens.view_offer

import com.appabilities.sold.base.BasePresenter
import com.appabilities.sold.networking.NetController
import com.appabilities.sold.networking.response.OfferResponse
import com.appabilities.sold.networking.services.RequestService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import java.net.HttpURLConnection.HTTP_OK

/**
 * Created by Hamza on 3/29/2018.
 */
class ViewOfferPresenter(view: ViewOfferContract.View) : BasePresenter<ViewOfferContract.View>(view), ViewOfferContract.Actions {

    override fun initScreen() {
        _view.setupViews()
    }

    override fun getRequestOffers(productID: String) {
        _view.showLoading()
        val call = NetController.getInstance().retrofit.create(RequestService::class.java)
                .getRequestOffers(productID)
        call.enqueue(object : Callback<List<OfferResponse>>{
            override fun onFailure(call: Call<List<OfferResponse>>?, t: Throwable?) {
                _view.showFailure("Unknown error occurred")
            }

            override fun onResponse(call: Call<List<OfferResponse>>?, response: Response<List<OfferResponse>>?) {
                if (response?.code() == HTTP_OK){
                    _view.showSuccess(response.body())
                }else if (response?.code() == HTTP_NOT_FOUND){
                    _view.showEmpty("No Offer Found")
                }else {
                    _view.showFailure(NetController.getError(response?.errorBody()).msg)
                }
            }
        })
    }

}