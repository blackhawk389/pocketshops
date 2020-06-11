package com.appabilities.sold.screens.select_categories

import com.appabilities.sold.networking.response.CategoriesResponse
import com.appabilities.sold.networking.response.VerifyUserResponse

/**
 * Created by Hamza on 9/15/2017.
 */
interface SelectCategoriesContract {

    /*
   View - this defines the methods that the pure views like Activity or Fragments etc will implement.
    */
    interface View {
        fun setupViews()
        fun onLoading()
        fun onRequestSuccessful(responseData: List<CategoriesResponse>)
        fun onRequestFail(errorMessage: String)
        fun onRequestNotFound()
        fun onSuccessfullySelectedCategories(body: VerifyUserResponse, finalSelection: String)
    }

    /*
    Actions - this defines the methods the pure Presenter class will implement. Also known as user actions,
    this is where the app logic is defined.
     */
    interface Actions {
        fun initScreen()
        fun getProductCategories()
        fun sendSelectedCategories(accessToken: String, selections: List<String>)
    }

    /*
    this defines the methods that pure model or persistence class like database or server data will implement.
     */
    interface Repository

}