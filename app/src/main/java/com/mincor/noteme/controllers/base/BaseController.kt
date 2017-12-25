package com.mincor.dancehalldancer.controllers.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.github.salomonbrys.kodein.KodeinInjected
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein

abstract class BaseController : Controller, KodeinInjected {

    constructor():super()
    constructor(bundle: Bundle):super(bundle)

    override val injector = KodeinInjector()

    override fun onAttach(view: View) {
        super.onAttach(view)
        attachListeners()
        inject(activity!!.appKodein())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return getControllerUI(inflater.context)
    }

    abstract fun getControllerUI(context: Context):View

    override fun onDetach(view: View) {
        detachListeners()
        super.onDetach(view)
    }

    /**
     * Назначаем слушателей для текущего Контроллера
     */
    protected open fun attachListeners() {

    }

    /**
     * Удаляем слушателей для текущего контроллера
     */
    protected open fun detachListeners() {

    }
}
