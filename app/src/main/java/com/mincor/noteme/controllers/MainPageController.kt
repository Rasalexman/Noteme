package com.mincor.noteme.controllers

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.EditText
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.github.salomonbrys.kodein.instance
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.listeners.EventHook
import com.mincor.dancehalldancer.controllers.base.BaseHeadRecyclerController
import com.mincor.noteme.R
import com.mincor.noteme.mvp.contracts.MainPageContract
import com.mincor.noteme.utils.color
import com.mincor.noteme.view.NoteItem
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.sdk25.coroutines.onClick
import utils.PHOperator

/**
 * Created by alexander on 01.11.17.
 */
class MainPageController : BaseHeadRecyclerController(),
        MainPageContract.View,
        SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    // строка поиска которой назначаем слушатель
    private var searchView: SearchView? = null
    // Title
    override val title: String get() = activity!!.getString(R.string.app_name)

    // IPresenter
    override val presenter: MainPageContract.Presenter by instance()

    // BASE UI
    override fun getControllerUI(context: Context): View = MainUI().createView(AnkoContext.Companion.create(context, this))

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.view = this
        presenter.getAllNotes()
    }

    override fun onItemClickHandler(item: AbstractItem<*, *>, position: Int) {
        router.pushController(RouterTransaction.with(AddNoteController((item as NoteItem).note))
                .pushChangeHandler(HorizontalChangeHandler()).popChangeHandler(HorizontalChangeHandler()))
    }

    override fun onDetach(view: View) {
        presenter.stop()
        searchView?.setOnQueryTextListener(null)
        searchView = null
        super.onDetach(view)
    }

    override fun showNotes(notes: List<NoteItem>) {
        print("all notes comes $notes")
        hideLoadingFooter()
        mFastItemAdapter?.add(notes)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // очищаем меню от уже ранее добавленных элементов
        menu.clear()
        // создаем новое меню
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        val search = menu.findItem(R.id.app_bar_search)
        this.searchView = search.actionView as SearchView
        searchView!!.setOnQueryTextListener(this)

        val searchSourceText:EditText = searchView!!.find(android.support.v7.appcompat.R.id.search_src_text)
        searchSourceText.setTextColor(Color.WHITE)
        searchSourceText.setHintTextColor(Color.WHITE)
    }

    override fun onQueryTextSubmit(s: String): Boolean {
        search(s)
        return true
    }

    override fun onQueryTextChange(s: String): Boolean = true
    override fun onClose(): Boolean = false
    private fun search(s:String) {
        if(s.isNotBlank() && s.isNotEmpty()){

        }
    }

    inner class MainUI : AnkoComponent<MainPageController> {
        override fun createView(ui: AnkoContext<MainPageController>): View = with(ui){
            coordinatorLayout {
                lparams(org.jetbrains.anko.matchParent, org.jetbrains.anko.matchParent)

                appBarLayout {
                    toolbar = toolbar {
                        id = com.mincor.noteme.R.id.main_appbar
                        setTitleTextColor(color(com.mincor.noteme.R.color.colorWhite))
                    }.lparams(org.jetbrains.anko.matchParent, dip(56)){
                        scrollFlags = android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                    }
                }.lparams(org.jetbrains.anko.matchParent)

                rvc = recyclerView {
                    id = com.mincor.noteme.R.id.rv_controller
                }.lparams(org.jetbrains.anko.matchParent, org.jetbrains.anko.matchParent) {
                    behavior = android.support.design.widget.AppBarLayout.ScrollingViewBehavior()
                }

                floatingActionButton {
                    id = com.mincor.noteme.R.id.button_add_note_id
                    setImageResource(R.drawable.ic_add_white_24dp)
                    onClick {
                        router.pushController(RouterTransaction.with(AddNoteController())
                                .pushChangeHandler(HorizontalChangeHandler()).popChangeHandler(HorizontalChangeHandler()))
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