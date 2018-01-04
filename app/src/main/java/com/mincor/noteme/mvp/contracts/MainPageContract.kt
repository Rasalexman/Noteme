package com.mincor.noteme.mvp.contracts

import com.mincor.noteme.mvp.base.IBasePresenter
import com.mincor.noteme.mvp.base.IBaseView
import com.mincor.noteme.view.NoteItem

/**
 * Created by alexander on 01.11.17.
 */
interface MainPageContract {

    interface Presenter:IBasePresenter<View>{
        fun search(s:String)
        fun clearSearch()
        fun clearSearchString()
    }
    interface View:IBaseView<Presenter>{
        fun showNotes(notes:List<NoteItem>)
        fun showLoadingFooter()
        fun applySearchTitle(ttl:String)
    }
}