package com.appabilities.sold.screens.view_offer

import com.appabilities.sold.networking.response.OfferResponse

/**
 * Created by Hamza on 3/29/2018.
 */
interface ViewOfferContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    interface View {
        fun setupViews()
        fun showFailure(s: String)
        fun showSuccess(body: List<OfferResponse>?)
        fun showEmpty(s: String)
        fun onClickAddOffer()
        fun showLoading()
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    interface Actions {
        fun initScreen()
        fun getRequestOffers(productID: String)
    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    interface Repository {

    }

}