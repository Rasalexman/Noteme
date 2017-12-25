package com.mincor.noteme.common

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.v7.widget.AppCompatTextView
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import com.mincor.noteme.R
import com.mincor.noteme.utils.string


/**
 * Created by a.minkin on 01.11.2017.
 */
class ExpandableTextView : AppCompatTextView {

    private val TAG = "ExpandableTextView"
    private val ELLIPSIZE = "... "
    private val DELIMETER = " "
    private val MORE = "more..."
    private val LESS = "less"

    private var mFullText: String? = null
    private var mMaxLines: Int = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun makeExpandable(maxLines: Int) {
        makeExpandable(text.toString(), maxLines)
    }

    fun makeExpandable(fullText: String, maxLines: Int) {
        mFullText = fullText
        mMaxLines = maxLines
        val vto = viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val obs = viewTreeObserver
                obs.removeOnGlobalLayoutListener(this)
                if (lineCount > mMaxLines) {
                    movementMethod = LinkMovementMethod.getInstance()
                    showLess()
                } else {
                    text = mFullText
                }
            }
        })
    }

    /**
     * truncate text and append a clickable [.MORE]
     */
    private fun showLess() {
        val lineEndIndex = layout.getLineEnd(mMaxLines - 1)
        val newText = (mFullText!!.substring(0, lineEndIndex - (ELLIPSIZE.length + MORE.length + 1)) + ELLIPSIZE + MORE)
        val builder = SpannableStringBuilder(newText)
        builder.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                showMore()
            }
        }, newText.length - MORE.length, newText.length, 0)
        setText(builder, TextView.BufferType.SPANNABLE)
    }

    /**
     * show full text and append a clickable [.LESS]
     */
    private fun showMore() {
        // create a text like subText + ELLIPSIZE + MORE
        val builder = SpannableStringBuilder(mFullText + DELIMETER + LESS)
        builder.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                showLess()
            }
        }, builder.length - LESS.length, builder.length, 0)
        setText(builder, TextView.BufferType.SPANNABLE)
    }

}