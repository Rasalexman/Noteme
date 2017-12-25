package com.mincor.noteme.mvp.presenters

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import com.mincor.noteme.mvp.contracts.AddNoteContract
import com.mincor.noteme.mvp.models.NoteModel
import com.raizlabs.android.dbflow.kotlinextensions.insert
import com.raizlabs.android.dbflow.kotlinextensions.update
import java.util.*

/**
 * Created by alexander on 10.11.17.
 */
class AddNotePresenter : AddNoteContract.IPresenter {

    override var view: AddNoteContract.IView? = null

    override fun start() {

    }

    override fun stop() {
        view = null
    }

    override fun updateNote(note:NoteModel) {
        note.update()
    }

    override fun saveNote(title: String?, text: String?) {
        val dt = Date()
        val newNote = NoteModel(0, title, text, dt, dt)
        newNote.insert()
    }
}

val addNotePageModule = Kodein.Module {
    bind<AddNoteContract.IPresenter>() with singleton { AddNotePresenter() }
}