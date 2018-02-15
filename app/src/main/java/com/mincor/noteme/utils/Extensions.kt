package com.mincor.noteme.utils

/**
 * Created by alexander on 06.11.17.
 */
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.view.ContextThemeWrapper
import android.view.View
import android.view.View.*
import android.view.ViewManager
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.RadioButton
import android.widget.ToggleButton
import com.mincor.noteme.common.ExpandableTextView
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.displayMetrics
import java.text.DateFormat
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by a.minkin on 28.09.2017.
 */

/**
 * Button with custom style
 * */
@SuppressLint("RestrictedApi")
inline fun ViewManager.styledButton(text: CharSequence?, styleRes: Int = 0, init: Button.() -> Unit): Button {
    return ankoView({ if (styleRes == 0) Button(it) else Button(ContextThemeWrapper(it, styleRes), null, 0) }, 0) {
        init()
        setText(text)
    }
}
@SuppressLint("RestrictedApi")
inline fun ViewManager.styledButton(textres: Int, styleRes: Int = 0, init: Button.() -> Unit): Button {
    return ankoView({ if (styleRes == 0) Button(it) else Button(ContextThemeWrapper(it, styleRes), null, 0) }, 0) {
        init()
        setText(textres)
    }
}
/**
 * Styled TextInputs
 * */
@SuppressLint("RestrictedApi")
inline fun ViewManager.styledAutoTextView(styleRes: Int = 0, init: AutoCompleteTextView.() -> Unit): AutoCompleteTextView {
    return ankoView({ if (styleRes == 0) AutoCompleteTextView(it) else AutoCompleteTextView(ContextThemeWrapper(it, styleRes), null, 0) }, 0) {
        init()
    }
}

/**
 * Radio Buttons with custom style
 * */
@SuppressLint("RestrictedApi")
inline fun ViewManager.styledRadioButton(textres: Int, styleRes: Int = 0, init: RadioButton.() -> Unit): RadioButton {
    return ankoView({ if (styleRes == 0) RadioButton(it) else RadioButton(ContextThemeWrapper(it, styleRes), null, 0) }, 0) {
        init()
        setText(textres)
    }
}

/**
 * Toggle Button Style
 * */
@SuppressLint("RestrictedApi")
inline fun ViewManager.styledToggleButton(textres: Int, styleRes: Int = 0, init: ToggleButton.() -> Unit): ToggleButton {
    return ankoView({ if (styleRes == 0) ToggleButton(it) else ToggleButton(ContextThemeWrapper(it, styleRes), null, 0) }, 0) {
        init()
        setText(textres)
    }
}

inline fun ViewManager.expandableTextView(init: ExpandableTextView.()->Unit):ExpandableTextView {
    return ankoView({ ExpandableTextView(it) }, 0) {
        init()
    }
}

/**
 * UTILS SECTION
 * */
fun View.drawable(@DrawableRes resource: Int): Drawable? = ContextCompat.getDrawable(context, resource)
fun View.color(@ColorRes resource: Int): Int = ContextCompat.getColor(context, resource)
fun View.string(stringRes:Int):String = context.getString(stringRes)
fun Context.wdthProc(proc:Float):Int = (this.displayMetrics.widthPixels*proc).toInt()
fun View.wdthProc(proc:Float):Int = (context.displayMetrics.widthPixels*proc).toInt()
fun Context.hdthProc(proc:Float):Int = (this.displayMetrics.heightPixels*proc).toInt()
fun View.hdthProc(proc:Float):Int = (context.displayMetrics.heightPixels*proc).toInt()

/***
 * Custom IView For somethings like lines
 * */
fun roundedBg(round:Float = 16f, col:Int = Color.WHITE, strk:Int = 0, strkColor:Int = Color.LTGRAY) = GradientDrawable().apply {
    shape = GradientDrawable.RECTANGLE
    cornerRadius = round
    setColor(col)
    if(strk > 0) setStroke(strk, strkColor)
}

/**
 * Sets the IView's visibility to INVISIBLE
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Toggle's IView's visibility. If IView is visible, then sets to gone. Else sets Visible
 */
fun View.toggle() {
    visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
}
var View.visible
    get() = visibility == VISIBLE
    set(value) {
        visibility = if (value) VISIBLE else GONE
    }

fun View.hide(gone: Boolean = true) {
    visibility = if (gone) GONE else INVISIBLE
}
fun View.show() {
    visibility = VISIBLE
}

object DateHelper {
    const val DF_SIMPLE_STRING = "HH:mm dd.MM.yyyy"
    @JvmField val DF_SIMPLE_FORMAT = object : ThreadLocal<DateFormat>() {
        override fun initialValue(): DateFormat {
            return SimpleDateFormat(DF_SIMPLE_STRING, Locale.getDefault())
        }
    }
}

fun dateNow(): String = Date().asString()

fun timestamp(): Long = System.currentTimeMillis()

fun dateParse(s: String): Date = DateHelper.DF_SIMPLE_FORMAT.get().parse(s, ParsePosition(0))

fun Date.asString(format: DateFormat): String = format.format(this)

fun Date.asString(format: String): String = asString(SimpleDateFormat(format, Locale.US))

fun Date.asString(): String = DateHelper.DF_SIMPLE_FORMAT.get().format(this)

fun Long.asDateString(): String = Date(this).asString()