package com.mincor.noteme.controllers

import android.content.Context
import android.view.View
import android.widget.EditText
import com.github.salomonbrys.kodein.instance
import com.mincor.noteme.R
import com.mincor.noteme.mvp.contracts.AddNoteContract
import com.mincor.noteme.mvp.models.NoteModel
import com.mincor.noteme.utils.color
import com.mincor.noteme.utils.string
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import ru.fortgroup.dpru.controllers.base.BaseActionBarController

/**
 * Created by alexander on 08.11.17.
 */
class AddNoteController(note: NoteModel? = null) : BaseActionBarController(bundleOf(Pair(NOTE_ID, note))), AddNoteContract.IView {

    companion object {
        val NOTE_ID: String = "note_id"
    }

    ///--- PRESENTER
    override val presenter: AddNoteContract.IPresenter by instance()

    ///--- UI
    override fun getControllerUI(context: Context) = AddNoteUI().createView(AnkoContext.Companion.create(context, this))

    ///--- TITLE FOR TOOLBAR
    override val title: String get() = args.getSerializable(NOTE_ID)?.let { activity!!.getString(R.string.edit_note_title) }?:activity!!.getString(R.string.add_note_title)

    private var noteToSave: NoteModel? = null

    private var titleText: EditText? = null
    private var bodyText: EditText? = null

    override fun onAttach(view: View) {
        super.onAttach(view)
        setHomeButtonEnable()
        presenter.view = this

        noteToSave = args.getSerializable(NOTE_ID)?.let { it as NoteModel }
        noteToSave?.let {
            showNote(it)
        }
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        presenter.stop()
        noteToSave = null
        titleText = null
        bodyText = null
    }

    private fun showNote(note: NoteModel) {
        noteToSave = note
        titleText?.setText(note.title)
        bodyText?.setText(note.text)
    }

    private fun onSaveClickHandler() {
        val title = titleText!!.text
        val body = bodyText!!.text
        if (body.isNotEmpty() && title.isNotEmpty()) {
            noteToSave?.let {
                it.title = title.toString()
                it.text = body.toString()
                presenter.updateNote(it)
            } ?: presenter.saveNote(title.toString(), body.toString())
            router.popCurrentController()
        }
    }

    override fun noteSaved() {}

    inner class AddNoteUI : AnkoComponent<AddNoteController> {
        override fun createView(ui: AnkoContext<AddNoteController>): View = with(ui) {
            verticalLayout {
                lparams(org.jetbrains.anko.matchParent, org.jetbrains.anko.matchParent)

                appBarLayout {
                    toolbar = toolbar {
                        id = com.mincor.noteme.R.id.main_appbar
                        setTitleTextColor(color(com.mincor.noteme.R.color.colorWhite))
                    }.lparams(org.jetbrains.anko.matchParent, dip(56))
                }.lparams(org.jetbrains.anko.matchParent)

                titleText = editText {
                    id = com.mincor.noteme.R.id.note_title_id
                    hint = string(com.mincor.noteme.R.string.note_title)
                    hintTextColor = color(com.mincor.noteme.R.color.secondaryTextColor)
                }.lparams(org.jetbrains.anko.matchParent) {
                    setMargins(dip(8), dip(16), dip(8), dip(8))
                }

                bodyText = editText {
                    id = com.mincor.noteme.R.id.note_body_id
                    hint = string(com.mincor.noteme.R.string.note_body)
                    hintTextColor = color(com.mincor.noteme.R.color.secondaryTextColor)
                    gravity = android.view.Gravity.TOP
                }.lparams(org.jetbrains.anko.matchParent, org.jetbrains.anko.matchParent) {
                    setMargins(dip(8), 0, dip(8), 0)
                    weight = 100f
                }

                button(com.mincor.noteme.R.string.save_article) {
                    id = com.mincor.noteme.R.id.save_button_id
                    onClick {
                        onSaveClickHandler()
                    }
                }.lparams(org.jetbrains.anko.matchParent, dip(56)) {
                    margin = dip(16)
                    weight = 1f
                }
            }
        }
    }
}