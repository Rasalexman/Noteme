package com.mincor.noteme.controllers

import android.content.Context
import android.graphics.Color
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.github.salomonbrys.kodein.instance
import com.google.firebase.analytics.FirebaseAnalytics
import com.mikepenz.fastadapter.items.AbstractItem
import com.mincor.dancehalldancer.controllers.base.BaseHeadRecyclerController
import com.mincor.noteme.R
import com.mincor.noteme.activity.MainActivity
import com.mincor.noteme.mvp.contracts.MainPageContract
import com.mincor.noteme.utils.color
import com.mincor.noteme.utils.visible
import com.mincor.noteme.view.NoteItem
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.sdk25.coroutines.onClick
import android.os.Bundle




/**
 * Created by alexander on 01.11.17.
 */
class MainPageController : BaseHeadRecyclerController(),
        MainPageContract.View,
        SearchView.OnQueryTextListener {

    // строка поиска которой назначаем слушатель
    private var searchView: SearchView? = null
    private var searchSourceText: EditText? = null
    private var searchMenuItem: MenuItem? = null
    private var deleteMenuItem: MenuItem? = null
    private var closeButton: ImageView? = null

    private var floatButton: FloatingActionButton? = null

    private val selectedItems = mutableListOf<NoteItem>()
    // Title
    override var title: String = ""
        get() = if (field.isEmpty()) activity!!.getString(R.string.all_note_title) else field

    // IPresenter
    override val presenter: MainPageContract.Presenter by instance()

    // search title
    private var sTitle: String = ""

    // BASE UI
    override fun getControllerUI(context: Context): View = MainUI().createView(AnkoContext.Companion.create(context, this))

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.view = this
        presenter.start()
    }

    override fun onItemClickHandler(item: AbstractItem<*, *>, position: Int) {
        router.pushController(RouterTransaction.with(AddNoteController((item as NoteItem).note))
                .pushChangeHandler(HorizontalChangeHandler()).popChangeHandler(HorizontalChangeHandler()))
    }

    override fun onSelectionChanged(item: AbstractItem<*, *>?, selected: Boolean) {
        val noteItem = item as NoteItem
        noteItem.itemSelected = selected
        mFastItemAdapter?.notifyAdapterItemChanged(mFastItemAdapter?.getPosition(noteItem) ?: 0)

        if(selected) {
            selectedItems.add(noteItem)
        } else {
            if(selectedItems.indexOf(noteItem) >= 0){
                selectedItems.remove(noteItem)
            }
        }
        checkDeleteMenuVisible()
    }

    private fun checkDeleteMenuVisible() {
        deleteMenuItem?.isVisible = (selectedItems.size > 0)
    }

    override fun onDetach(view: View) {
        presenter.stop()
        deleteMenuItem?.setOnMenuItemClickListener(null)
        deleteMenuItem = null
        searchView?.setOnQueryTextListener(null)
        searchView?.setOnSearchClickListener(null)
        searchView = null
        searchMenuItem?.setOnActionExpandListener(null)
        searchMenuItem = null
        closeButton?.setOnClickListener(null)
        closeButton = null
        super.onDetach(view)
    }

    override fun onDestroy() {
        floatButton?.setOnClickListener(null)
        floatButton = null
        super.onDestroy()
    }

    override fun applySearchTitle(ttl: String) {
        this.title = ttl
        sTitle = ttl
        setTitle()
    }

    override fun showNotes(notes: List<NoteItem>) {
        print("all notes comes $notes")
        hideLoadingFooter()
        mFastItemAdapter?.set(notes)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // очищаем меню от уже ранее добавленных элементов
        menu.clear()
        // создаем новое меню
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        searchMenuItem = menu.findItem(R.id.app_bar_search)
        deleteMenuItem = menu.findItem(R.id.app_bar_delete)
        deleteMenuItem!!.setOnMenuItemClickListener {
            selectedItems.forEach {
                mFastItemAdapter?.remove(mFastItemAdapter?.getPosition(it)?:0)
                presenter.deleteItem(it)
            }
            selectedItems.clear()
            deleteMenuItem?.isVisible = false
            true
        }

        checkDeleteMenuVisible()

        this.searchView = searchMenuItem!!.actionView as SearchView
        searchView!!.setOnQueryTextListener(this)

        searchSourceText = searchView!!.find(android.support.v7.appcompat.R.id.search_src_text)
        searchSourceText!!.setTextColor(Color.WHITE)
        searchSourceText!!.setHintTextColor(Color.WHITE)

        searchView!!.setOnSearchClickListener {
            searchSourceText?.setText(sTitle)
            searchSourceText?.setSelection(sTitle.length)
        }

        closeButton = this.searchView!!.find(R.id.search_close_btn) as ImageView
        // Set on click listener
        closeButton!!.setOnClickListener {
            if (sTitle.isNotEmpty()) {
                clearSearch()
            }
            searchSourceText?.setText("")
            searchSourceText?.requestFocusFromTouch()
        }
    }

    private fun clearSearch() {
        applySearchTitle("")
        this.mFastItemAdapter?.clear()
        presenter.clearSearch()
    }

    override fun onQueryTextSubmit(s: String): Boolean {
        if (s.isNotBlank() && s.isNotEmpty()) {
            applySearchTitle(s)
            this.searchView?.clearFocus()
            this.mFastItemAdapter?.clear()
            presenter.search(s)

            ///---- LOG ANALYTICS
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, s)
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "text")
            val analytics = (this.activity as MainActivity).mFirebaseAnalytics
            analytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
        }
        return true
    }

    private fun onAddNewNoteHandler() {
        title = ""
        sTitle = ""
        presenter.clearSearchString()
        router.pushController(RouterTransaction.with(AddNoteController())
                .pushChangeHandler(HorizontalChangeHandler()).popChangeHandler(HorizontalChangeHandler()))
    }

    override fun onQueryTextChange(s: String): Boolean = false

    inner class MainUI : AnkoComponent<MainPageController> {
        override fun createView(ui: AnkoContext<MainPageController>): View = with(ui) {
            coordinatorLayout {
                lparams(org.jetbrains.anko.matchParent, org.jetbrains.anko.matchParent)

                appBarLayout {
                    toolbar = toolbar {
                        id = com.mincor.noteme.R.id.main_appbar
                        setTitleTextColor(color(com.mincor.noteme.R.color.colorWhite))
                    }.lparams(org.jetbrains.anko.matchParent, dip(56)) {
                        scrollFlags = android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                    }
                }.lparams(org.jetbrains.anko.matchParent)

                rvc = recyclerView {
                    id = com.mincor.noteme.R.id.rv_controller
                }.lparams(org.jetbrains.anko.matchParent, org.jetbrains.anko.matchParent) {
                    behavior = android.support.design.widget.AppBarLayout.ScrollingViewBehavior()
                }

                floatButton = floatingActionButton {
                    id = com.mincor.noteme.R.id.button_add_note_id
                    setImageResource(R.drawable.ic_add_white_24dp)
                    onClick {
                        onAddNewNoteHandler()
                    }
                }.lparams {
                    anchorId = com.mincor.noteme.R.id.rv_controller
                    anchorGravity = android.view.Gravity.BOTTOM or android.view.Gravity.END
                    behavior = com.mincor.noteme.common.behavior.ScrollAwareFABBehavior()
                    margin = dip(16)
                }
            }
        }
    }
}