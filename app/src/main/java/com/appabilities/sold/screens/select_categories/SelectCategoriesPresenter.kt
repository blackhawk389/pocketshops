package com.appabilities.sold.screens.select_categories

import android.content.Intent

import com.appabilities.sold.base.BasePresenter
import com.appabilities.sold.networking.NetController
import com.appabilities.sold.networking.response.CategoriesResponse
import com.appabilities.sold.networking.response.VerifyUserResponse

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.net.HttpURLConnection.HTTP_NOT_FOUND
import java.net.HttpURLConnection.HTTP_OK

/**
 * Created by Hamza on 9/15/2017.
 */
class SelectCategoriesPresenter(view: SelectCategoriesContract.View) : BasePresenter<SelectCategoriesContract.View>(view), SelectCategoriesContract.Actions {

    internal var selections = ""

    override fun initScreen() {
        _view.setupViews()
    }

    override fun getProductCategories() {
        val call = NetController.getInstance().categoriesService.categoriesList
        call.enqueue(object : Callback<List<CategoriesResponse>> {
            override fun onResponse(call: Call<List<CategoriesResponse>>, response: Response<List<CategoriesResponse>>) {
                if (response.code() == HTTP_OK) {
                    _view.onRequestSuccessful(response.body()!!)
                } else if (response.code() == HTTP_NOT_FOUND) {
                    _view.onRequestNotFound()
                } else {
                    _view.onRequestFail("")
                }
            }

            override fun onFailure(call: Call<List<CategoriesResponse>>, t: Throwable) {
                _view.onRequestFail(t.localizedMessage)
            }
        })
    }

    override fun sendSelectedCategories(accessToken: String, catSelections: List<String>) {
        _view.onLoading()
        if (catSelections.size > 0) {
            for (i in catSelections.indices)
                selections += catSelections[i] + ","
            selections = selections.substring(0, selections.lastIndexOf(','))
        }
        val call = NetController.getInstance().categoriesService.addCategorySelections(accessToken, selections)
        call.enqueue(object : Callback<VerifyUserResponse> {
            override fun onResponse(call: Call<VerifyUserResponse>, response: Response<VerifyUserResponse>) {
                if (response.code() == HTTP_OK)
                    _view.onSuccessfullySelectedCategories(response.body()!!, selections)
            }

            override fun onFailure(call: Call<VerifyUserResponse>, t: Throwable) {
                _view.onRequestFail(t.localizedMessage)
            }
        })
    }
}