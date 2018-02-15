package com.mincor.noteme.view

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mincor.noteme.R
import com.mincor.noteme.mvp.models.NoteModel
import com.mincor.noteme.utils.asString
import com.mincor.noteme.utils.color
import com.mincor.noteme.utils.roundedBg
import org.jetbrains.anko.*


/**
 * Created by alexander on 07.11.17.
 */
class NoteItem(val note:NoteModel) : AbstractItem<NoteItem, NoteItem.ViewHolder>() {

    override fun createView(ctx: Context, parent: ViewGroup?) = NoteItemUI().createView(AnkoContext.Companion.create(ctx, this))
    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)
    override fun getType(): Int = R.id.note_item_id
    override fun getLayoutRes(): Int = -1

    var itemSelected = false

    inner class NoteItemUI : AnkoComponent<NoteItem> {



        override fun createView(ui: AnkoContext<NoteItem>): View = with(ui) {
            verticalLayout {
                lparams(matchParent, wrapContent) {
                    setMargins(dip(8), dip(4), dip(8), dip(4))
                }
                //background = roundedBg(8f, color(R.color.colorGrayLight), 2, color(R.color.colorDarkGray))

                textView {
                    id = R.id.note_title
                    textSize = 14f
                    typeface = Typeface.DEFAULT_BOLD
                }.lparams(matchParent) {
                    setMargins(dip(8), dip(8), dip(8), 0)
                }


                textView {
                    id = R.id.note_date
                    textSize = 10f
                    textColor = color(R.color.secondaryTextColor)
                }.lparams(matchParent) {
                    setMargins(dip(8), 0, dip(8), dip(4))
                }

                textView {
                    id = R.id.note_text
                    textSize = 12f
                }.lparams(matchParent) {
                    setMargins(dip(8), 0, dip(8), dip(8))
                }
            }
        }
    }

    class ViewHolder(view: View) : FastAdapter.ViewHolder<NoteItem>(view) {

        var noteTitle: TextView = view.find(R.id.note_title)
        private var noteDate: TextView = view.find(R.id.note_date)
        private var noteText: TextView = view.find(R.id.note_text)

        private val selectedBack = with(view) {
            roundedBg(8f, color(R.color.secondaryColor), 2, color(R.color.colorDarkGray))
        }
        private val unselectedBack = with(view){
            roundedBg(8f, color(R.color.colorGrayLight), 2, color(R.color.colorDarkGray))
        }

        override fun bindView(item: NoteItem, payloads: MutableList<Any>?) {
            itemView.background = if(item.itemSelected) selectedBack else unselectedBack
            noteTitle.text = item.note.title
            noteDate.text = item.note.createDate?.asString() ?: ""
            noteText.text = if(item.note.text?.length?:0 > 124) item.note.text?.substring(0..124)+"..." else item.note.text
        }

        override fun unbindView(item: NoteItem?) {
            noteTitle.text = null
            noteDate.text = null
            noteText.text = null
            itemView.background = null
        }
    }
}