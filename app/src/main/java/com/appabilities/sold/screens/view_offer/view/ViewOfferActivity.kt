package com.appabilities.sold.screens.view_offer.view

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.appabilities.sold.R
import com.appabilities.sold.base.BaseActivity
import com.appabilities.sold.databinding.ActivityViewOfferBinding
import com.appabilities.sold.databinding.OfferItemLayoutBinding
import com.appabilities.sold.databinding.UserOfferItemLayoutBinding
import com.appabilities.sold.networking.response.ImgResponse
import com.appabilities.sold.networking.response.OfferResponse
import com.appabilities.sold.screens.add_offer.view.AddOfferActivity
import com.appabilities.sold.screens.view_offer.ViewOfferContract
import com.appabilities.sold.screens.view_offer.ViewOfferPresenter
import com.appabilities.sold.utils.AppConstants
import com.appabilities.sold.utils.SnackUtils
import com.kennyc.view.MultiStateView
import com.stfalcon.frescoimageviewer.ImageViewer
import com.thetechnocafe.gurleensethi.liteutils.RecyclerAdapterUtil

class ViewOfferActivity : BaseActivity(), ViewOfferContract.View {

    lateinit var bi: ActivityViewOfferBinding
    lateinit var presenter: ViewOfferPresenter
    lateinit var mAdapter: RecyclerAdapterUtil<OfferResponse>
    var mOfferList =  ArrayList<OfferResponse>()
    var productID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bi = DataBindingUtil.setContentView(this, R.layout.activity_view_offer)
        bi.view = this
        presenter = ViewOfferPresenter(this)
        bi.presenter = presenter
        presenter.initScreen()
    }

    override fun setupViews() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "VIEW OFFERS"

        bi.recyclerView.setHasFixedSize(true)
        bi.recyclerView.layoutManager =  LinearLayoutManager(this)

        mAdapter = RecyclerAdapterUtil(this, mOfferList, R.layout.user_offer_item_layout)
        mAdapter.addOnDataBindListener { itemView, item, position, innerViews ->
            val binding = DataBindingUtil.bind<UserOfferItemLayoutBinding>(itemView)
            binding?.offerItem = item
            binding?.executePendingBindings()

            binding?.txtTime?.text = AppConstants.dateDiff(item.createdOn)
            if(item.imgNames != null){
                binding?.imgProduct?.setImageURI(item.imgNames.get(0).imgName)
            }
            if (item.subCategoryName != null)
               binding?.txtCategory?.setText(item.categoryName +"->"+item.subCategoryName);
            else
                binding?.txtCategory?.setText(item.categoryName);
        }
        mAdapter.addOnClickListener { item, position ->
            if(item.imgNames != null){
                ImageViewer.Builder<ImgResponse>(this, item.imgNames)
                        .setFormatter(ImageViewer.Formatter<ImgResponse> { customImage -> customImage.imgName })
                        .show()
            }
        }
        bi.recyclerView.adapter = mAdapter

        productID = intent.extras.getString(AppConstants.KEYS.PRODUCT_ID.name, "");

    }

    override fun onResume() {
        super.onResume()
        presenter.getRequestOffers(productID)
    }

    override fun onClickAddOffer() {

        val intent = Intent(this@ViewOfferActivity, AddOfferActivity::class.java)
        intent.putExtra(AppConstants.KEYS.PRODUCT_ID.name, productID)
        startActivity(intent)

    }

    override fun showFailure(s: String) {
        SnackUtils.showErrorMessage(this, s)
        bi.multiSateView.viewState = MultiStateView.VIEW_STATE_ERROR
    }

    override fun showLoading() {
        bi.multiSateView.viewState = MultiStateView.VIEW_STATE_LOADING
    }


    override fun showSuccess(body: List<OfferResponse>?) {
        mOfferList.clear()
        mOfferList.addAll(body!! as ArrayList<OfferResponse>)
        mAdapter.notifyDataSetChanged()
        bi.multiSateView.viewState = MultiStateView.VIEW_STATE_CONTENT
    }

    override fun showEmpty(s: String) {
        bi.multiSateView.viewState = MultiStateView.VIEW_STATE_EMPTY
    }

}
