package com.mincor.noteme.mvp.presenters

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import com.mincor.noteme.mvp.contracts.MainPageContract
import com.mincor.noteme.mvp.models.NoteModel
import com.mincor.noteme.mvp.models.NoteModel_Table
import com.mincor.noteme.mvp.models.NoteModel_Table.*
import com.mincor.noteme.view.NoteItem
import com.raizlabs.android.dbflow.kotlinextensions.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

/**
 * Created by alexander on 01.11.17.
 */
class MainPagePresenter : MainPageContract.Presenter {

    override var view: MainPageContract.View? = null
    private val mAllNotes:MutableList<NoteItem> = mutableListOf()
    private var mSearchedNotes:MutableList<NoteItem> = mutableListOf()

    private var lastSearch:String = ""

    override fun start() {
        launch(UI) {
            view?.showLoadingFooter()

            if (lastSearch.isNotEmpty()) {
                view?.showNotes(mSearchedNotes)
                view?.applySearchTitle(lastSearch)
                return@launch
            }

            val result = async(CommonPool) {
                if(mAllNotes.size > 0) {
                    val first = mAllNotes.first()
                    (select from NoteModel::class where(createDate greaterThan first.note.createDate!!)).list
                }else {
                    (select from NoteModel::class).orderBy(NoteModel_Table.createDate, true).list
                }
            }
            createNoteItems(result.await(), mAllNotes)
            view?.showNotes(mAllNotes)
        }
    }

    override fun stop() {
        this.view = null
    }

    override fun search(s: String) {
        launch(UI) {
            lastSearch = s
            mSearchedNotes.clear()
            view?.showLoadingFooter()

            val result = async(CommonPool){
                val pattern = "%$s%"
                (select from NoteModel::class where(title.like(pattern) or text.like(pattern))).orderBy(NoteModel_Table.createDate, true).list
            }.await()

            createNoteItems(result, mSearchedNotes)
            view?.showNotes(mSearchedNotes)
        }
    }

    override fun clearSearch() {
        clearSearchString()
        start()
    }

    override fun clearSearchString() {
        lastSearch = ""
        mSearchedNotes.clear()
    }

    private suspend fun createNoteItems(notes:List<NoteModel>, mapper:MutableList<NoteItem>){
        notes.forEach {
            mapper.add(0, NoteItem(it))
        }
    }
}

val mainPageModule = Kodein.Module {
    bind<MainPageContract.Presenter>() with singleton { MainPagePresenter() }
}