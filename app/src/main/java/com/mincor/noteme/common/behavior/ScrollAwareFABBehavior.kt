package com.mincor.noteme.common.behavior

import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.support.v4.view.ViewCompat.setTranslationY
import android.support.design.widget.Snackbar




/**
 * Created by alexander on 07.11.17.
 */
class ScrollAwareFABBehavior : FloatingActionButton.Behavior() {

    private var isFabShowing:Boolean = true

    override fun layoutDependsOn(parent: CoordinatorLayout, child: FloatingActionButton, dependency: View): Boolean {
        return dependency is RecyclerView
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        Log.d("SCROLL DEBUG", "dyConsumed = $dyConsumed")
        if(isFabShowing && dyConsumed > 2){
            isFabShowing = false
            val translation = child.y - child.height - 10
            child.animate().translationYBy(translation).start()
        }else if(!isFabShowing && dyConsumed < -2){
            isFabShowing = true
            child.animate().translationY(0f).start()
        }
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }
}