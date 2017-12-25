package com.mincor.noteme.mvp.base

/**
 * Created by alexander on 01.11.17.
 */
interface IBaseView<out T : IBasePresenter<*>> {
    val presenter: T
}