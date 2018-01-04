package com.mincor.dancehalldancer.controllers.base

import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.OnClickListener
import com.mikepenz.fastadapter_extensions.items.ProgressItem
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener
import ru.fortgroup.dpru.controllers.base.BaseActionBarController

/**
 * Created by Alex on 07.01.2017.
 */

abstract class BaseHeadRecyclerController : BaseActionBarController(), OnClickListener<AbstractItem<*, *>> {

    init {
        retainViewMode = RetainViewMode.RETAIN_DETACH
    }

    open var rvc: RecyclerView? = null

    // manager
    protected var layoutManager: RecyclerView.LayoutManager? = null
    // save our FastAdapter
    protected var mFastItemAdapter: FastItemAdapter<AbstractItem<*, *>>? = null
    // endless update adapter Item
    protected var mFooterAdapter: ItemAdapter<AbstractItem<*,*>>? = null
    // saved scroll position
    private var savedScrollPosition = 0
    // Store a member variable for the listener
    protected var scrollListener: RecyclerView.OnScrollListener? = null

    override fun onAttach(view: View) {
        super.onAttach(view)
        createAdapters()                   // создаем адаптеры
        setRVLayoutManager()               // назначаем лайаут
        setRVCAdapter()                    // назначаем адаптер
        //addOnScrollListener()              // добавляем скролл
        checkData()                        // загружаем данные
        applyScrollPosition()              // назначаем сохраненную скролл позицию
    }

    protected fun createAdapters() {
        mFastItemAdapter = mFastItemAdapter?:FastItemAdapter()
        mFastItemAdapter!!.withOnClickListener(this)
        mFooterAdapter = mFooterAdapter?: ItemAdapter.items()
        mFastItemAdapter!!.addAdapter(1, mFooterAdapter)
    }

    protected fun setRVCAdapter() {
        rvc?.setHasFixedSize(true)
        rvc?.swapAdapter(mFastItemAdapter, true)
    }

    protected fun addOnScrollListener() {
        scrollListener = scrollListener?:object : EndlessRecyclerOnScrollListener(){
            override fun onLoadMore(currentPage: Int) {
                showLoadingFooter()
                Handler().postDelayed({
                    onLoadMoreBottomHandler(currentPage)
                }, 2000)
            }
        }
        rvc?.addOnScrollListener(scrollListener)
    }

    protected fun scrollToFirstIfNeeded() {
        if ((layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() <= 1) {
            layoutManager!!.scrollToPosition(0)
        }
        hideLoadingFooter()
    }

    protected fun onLoadMoreBottomHandler(currentPage: Int) {

    }

    // Показываем загрузку
    fun showLoadingFooter() {
        hideLoadingFooter()
        mFooterAdapter?.add(ProgressItem().withEnabled(false))
    }

    // прячем загрузку
    protected fun hideLoadingFooter() {
        mFooterAdapter?.clear()
    }

    protected fun applyScrollPosition() {
        if (layoutManager != null && savedScrollPosition > -1) {
            layoutManager!!.scrollToPosition(savedScrollPosition)
        }
    }

    override fun onClick(v: View, adapter: IAdapter<AbstractItem<*, *>>, item: AbstractItem<*, *>, position: Int): Boolean {
        onItemClickHandler(item, position)
        return false
    }

    protected open fun onItemClickHandler(item: AbstractItem<*, *>, position: Int){

    }

    protected fun setRVLayoutManager() {
        layoutManager = layoutManager?:LinearLayoutManager(this.activity, LinearLayout.VERTICAL, false)
        (layoutManager as LinearLayoutManager).isSmoothScrollbarEnabled = false
        rvc?.layoutManager = layoutManager
    }

    protected fun checkData() {
        showLoadingFooter()
    }

    protected fun setScrollPosition() {
        if (layoutManager != null) {
            savedScrollPosition = (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
            layoutManager = null
        }
    }

    override fun onDetach(view: View) {
        setScrollPosition()
        clearEndlessScrollListener()
        rvc?.recycledViewPool?.clear()
        mFastItemAdapter!!.notifyDataSetChanged()

        rvc?.adapter = null
        rvc?.removeAllViews()
        rvc?.removeAllViewsInLayout()
        rvc?.layoutManager = null

        if (mFooterAdapter != null) {
            mFooterAdapter!!.clear()
            mFooterAdapter = null
        }
        if (mFastItemAdapter != null) {
            mFastItemAdapter!!.withEventHook(null)
            mFastItemAdapter!!.clear()
            mFastItemAdapter!!.withOnClickListener(null)
            mFastItemAdapter = null
        }

        //rvc = null
        super.onDetach(view)
    }

    private fun clearEndlessScrollListener() {
        scrollListener?.let {
            rvc!!.clearOnScrollListeners()
            rvc!!.removeOnScrollListener(it)
            scrollListener = null
        }
    }
}
