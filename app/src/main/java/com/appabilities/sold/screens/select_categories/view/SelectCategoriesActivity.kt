package com.appabilities.sold.screens.select_categories.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.appabilities.sold.R
import com.appabilities.sold.base.BaseActivity
import com.appabilities.sold.databinding.ActivitySelectCategoriesBinding
import com.appabilities.sold.databinding.SelectCategoryItemLayoutBinding
import com.appabilities.sold.networking.response.CategoriesResponse
import com.appabilities.sold.networking.response.VerifyUserResponse
import com.appabilities.sold.screens.home.view.HomeActivity
import com.appabilities.sold.screens.select_categories.SelectCategoriesContract
import com.appabilities.sold.screens.select_categories.SelectCategoriesPresenter
import com.appabilities.sold.utils.SnackUtils
import com.kennyc.view.MultiStateView
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil
import java.util.ArrayList

class SelectCategoriesActivity : BaseActivity(), SelectCategoriesContract.View {

    lateinit var bi: ActivitySelectCategoriesBinding
    lateinit var presenter: SelectCategoriesPresenter
    var catSelections = ArrayList<String>()
    var categoryList = ArrayList<CategoriesResponse>()
    lateinit var adapter: RecyclerAdapterUtil<CategoriesResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bi = DataBindingUtil.setContentView(this, R.layout.activity_select_categories)
        bi.view = this
        presenter = SelectCategoriesPresenter(this)
        bi.presenter = presenter
        presenter.initScreen()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_select_category, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menuSelect_categories -> presenter.sendSelectedCategories(loginResponse.access_token, catSelections)
            else -> return super.onOptionsItemSelected(item)
        }
        return false
    }


    override fun setupViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ca.getResourceColor(R.color.colorPrimaryDark)
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        presenter.getProductCategories()
        adapter = RecyclerAdapterUtil(this, categoryList,
                R.layout.select_category_item_layout)

        adapter.addOnDataBindListener { view, categoriesResponse, position, integerMap ->
            val binding = DataBindingUtil.bind<SelectCategoryItemLayoutBinding>(view)
            binding!!.categoryModel = categoriesResponse
            binding!!.executePendingBindings()

            if (categoryList[position].is_selected == 1) {
                binding!!.imgCheck.visibility = View.VISIBLE
                //binding!!.overlayImg.visibility = View.VISIBLE


            } else {
                binding!!.imgCheck.visibility = View.INVISIBLE
                //binding!!.overlayImg.visibility = View.GONE

            }
        }

        adapter.addOnClickListener { categoriesResponse, position ->
            when(categoryList[position].is_selected){
                0->{
                    categoryList[position].is_selected = 1
                    catSelections.add(categoryList[position].categoryID)
                }
                1->{
                    categoryList[position].is_selected = 0
                    catSelections.remove(categoryList[position].categoryID)
                    /*catSelections.removeIf {
                        it == categoryList[position].categoryID
                    }*/
                }
            }

            Log.w("SOLD", "Cats: " + catSelections.toString())


            adapter.notifyDataSetChanged()
        }

        bi.recyclerCategories.layoutManager = LinearLayoutManager(this)
        bi.recyclerCategories.itemAnimator = DefaultItemAnimator()
        bi.recyclerCategories.adapter = adapter
    }

    override fun onLoading() {
        bi.multiSateView.viewState = MultiStateView.VIEW_STATE_LOADING
    }

    override fun onRequestSuccessful(responseData: List<CategoriesResponse>) {
        categoryList.addAll(responseData)
        catSelections.clear()

        if (!loginResponse.selected_category.isNullOrEmpty()){
            val splitSelectedCategory = loginResponse.selected_category.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (categoryID in splitSelectedCategory) {
                catSelections.add(categoryID)
                categoryList.forEach { item->
                    if (item.categoryID == categoryID) {
                        item.is_selected = 1
                    }
                }
            }
        }
        adapter.notifyDataSetChanged()
        bi.multiSateView.viewState = MultiStateView.VIEW_STATE_CONTENT
    }

    override fun onRequestFail(errorMessage: String) {
        bi.multiSateView.viewState = MultiStateView.VIEW_STATE_ERROR
    }

    override fun onRequestNotFound() {
        bi.multiSateView.viewState = MultiStateView.VIEW_STATE_EMPTY
    }

    override fun onSuccessfullySelectedCategories(body: VerifyUserResponse, finalSelections: String) {
        bi.multiSateView.viewState = MultiStateView.VIEW_STATE_CONTENT
        loginResponse.selected_category = finalSelections
        loginResponse.save()

        SnackUtils.showSnackMessage(this, body.msg, true)
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
