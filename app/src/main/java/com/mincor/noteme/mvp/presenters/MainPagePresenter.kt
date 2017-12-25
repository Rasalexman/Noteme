package com.mincor.noteme.mvp.presenters

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import com.mincor.noteme.mvp.contracts.MainPageContract
import com.mincor.noteme.mvp.models.NoteModel
import com.mincor.noteme.mvp.models.NoteModel_Table
import com.mincor.noteme.mvp.models.NoteModel_Table.createDate
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

    override fun start() {}

    override fun stop() {
        this.view = null
    }

    override fun getAllNotes() {
        launch(UI) {
            val result = async(CommonPool) {
                if(mAllNotes.size > 0) {
                    val first = mAllNotes.first()
                    (select from NoteModel::class where(createDate greaterThan first.note.createDate!!)).list
                }else {
                    (select from NoteModel::class).orderBy(NoteModel_Table.createDate, true).list
                }
            }
            val notesList = result.await()
            notesList.forEach { note->
                mAllNotes.add(0, NoteItem(note))
            }
            view?.showNotes(mAllNotes)
        }
    }
}

val mainPageModule = Kodein.Module {
    bind<MainPageContract.Presenter>() with singleton { MainPagePresenter() }
}