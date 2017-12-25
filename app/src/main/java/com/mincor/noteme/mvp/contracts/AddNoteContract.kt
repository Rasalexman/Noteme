package com.mincor.noteme.mvp.contracts

import com.mincor.noteme.mvp.base.IBasePresenter
import com.mincor.noteme.mvp.base.IBaseView
import com.mincor.noteme.mvp.models.NoteModel

/**
 * Created by alexander on 10.11.17.
 */
interface AddNoteContract {
    interface IPresenter : IBasePresenter<IView> {
        fun saveNote(title:String? = "", text:String? = "")
        fun updateNote(note:NoteModel)
    }
    interface IView : IBaseView<IPresenter> {
        fun noteSaved()
    }
}