package ru.fortgroup.dpru.controllers.base

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import com.mincor.dancehalldancer.controllers.base.BaseController
import com.mincor.dancehalldancer.view.actionbar.ActionBarProvider


/**
 * Created by alexander on 24.08.17.
 */

abstract class BaseActionBarController : BaseController {

    constructor():super()
    constructor(bundle: Bundle):super(bundle)

    open var toolbar: Toolbar? = null

    override fun onAttach(view: View) {
        super.onAttach(view)
        setActionBar()
        setTitle()
        setHasOptionsMenu(true)
    }

    protected fun setHomeButtonEnable() {
        //set the back arrow in the toolbar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onChangeStarted(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        super.onChangeStarted(changeHandler, changeType)
        setOptionsMenuHidden(!changeType.isEnter)
    }

    protected val actionBar: ActionBar?
        get() {
            val actionBarProvider = activity as ActionBarProvider?
            return actionBarProvider?.getSupportActionBar()
        }

    protected fun setActionBar() {
        toolbar?.let {
            (activity as ActionBarProvider).setSupportActionBar(it)
        }
    }

    protected fun setTitle() {
        toolbar?.title = title
    }

    protected abstract val title: String

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handle the click on the back arrow click
        return when (item.itemId) {
            android.R.id.home -> {
                goBack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDetach(view: View) {
       // (activity as ActionBarProvider).setSupportActionBar(null)
        super.onDetach(view)
    }

    override fun onDestroyView(view: View) {
        toolbar = null
        super.onDestroyView(view)
    }

    protected fun goBack() {
        router.popController(this)
    }
}
