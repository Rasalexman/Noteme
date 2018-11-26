package com.mincor.noteme.mvp.presenters

import com.dbflow5.structure.save
import com.dbflow5.structure.update
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import com.mincor.noteme.mvp.contracts.AddNoteContract
import com.mincor.noteme.mvp.models.NoteModel
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
        val newNote = NoteModel(0, title, text, dt, dt, title?.toLowerCase()+""+text?.toLowerCase())
        newNote.save()
    }
}

val addNotePageModule = Kodein.Module {
    bind<AddNoteContract.IPresenter>() with singleton { AddNotePresenter() }
}