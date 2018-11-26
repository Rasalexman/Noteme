package com.mincor.noteme.mvp.presenters

import com.dbflow5.query.list
import com.dbflow5.query.or
import com.dbflow5.query.select
import com.dbflow5.structure.delete
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import com.mincor.noteme.mvp.contracts.MainPageContract
import com.mincor.noteme.mvp.models.NoteModel
import com.mincor.noteme.mvp.models.NoteModel_Table
import com.mincor.noteme.mvp.models.NoteModel_Table.*
import com.mincor.noteme.view.NoteItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Created by alexander on 01.11.17.
 */
class MainPagePresenter : MainPageContract.Presenter, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override var view: MainPageContract.View? = null
    private val mAllNotes: MutableList<NoteItem> = mutableListOf()
    private var mSearchedNotes: MutableList<NoteItem> = mutableListOf()

    private var lastSearch: String = ""

    override fun start() {
        launch {
            view?.showLoadingFooter()

            if (lastSearch.isNotEmpty()) {
                view?.showNotes(mSearchedNotes)
                view?.applySearchTitle(lastSearch)
                return@launch
            }

            val result = async {
                if (mAllNotes.size > 0) {
                    val first = mAllNotes.first()
                    (select from NoteModel::class where (createDate greaterThan first.note.createDate!!)).list
                } else {
                    (select from NoteModel::class).orderBy(NoteModel_Table.createDate, true).list
                }
            }.await()

            async {
                createNoteItems(result, mAllNotes)
            }.await()

            view?.showNotes(mAllNotes)
        }
    }

    override fun stop() {
        this.view = null
    }

    override fun search(s: String) {
        launch {
            lastSearch = s
            mSearchedNotes.clear()
            view?.showLoadingFooter()

            val result = async {
                val pattern = "%$s%"
                (select from NoteModel::class where (title.like(pattern) or text.like(pattern))).orderBy(NoteModel_Table.createDate, true).list
            }.await()

            async {
                createNoteItems(result, mSearchedNotes)
            }.await()

            view?.showNotes(mSearchedNotes)
        }
    }

    override fun deleteItem(noteModel: NoteItem) {
        async {
            noteModel.note.delete()
        }

        if(mAllNotes.indexOf(noteModel) >= 0){
            mAllNotes.remove(noteModel)
        } else if(mSearchedNotes.indexOf(noteModel) >= 0){
            mSearchedNotes.remove(noteModel)
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

    private fun createNoteItems(notes: List<NoteModel>, mapper: MutableList<NoteItem>) {
        notes.forEach {
            mapper.add(0, NoteItem(it))
        }
    }
}

val mainPageModule = Kodein.Module {
    bind<MainPageContract.Presenter>() with singleton { MainPagePresenter() }
}