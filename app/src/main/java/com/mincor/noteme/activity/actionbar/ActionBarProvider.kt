package com.mincor.dancehalldancer.view.actionbar

import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar

interface ActionBarProvider {
    fun getSupportActionBar():ActionBar?
    fun setSupportActionBar(toolbar: Toolbar?)
}
