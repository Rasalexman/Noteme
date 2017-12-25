package com.mincor.noteme.mvp.base

/**
 * Created by alexander on 01.11.17.
 */
interface IBasePresenter<T> {
    fun start()
    fun stop()
    var view: T?
}