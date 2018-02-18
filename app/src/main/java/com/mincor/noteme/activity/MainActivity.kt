package com.mincor.noteme.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.google.firebase.analytics.FirebaseAnalytics
import com.mincor.dancehalldancer.view.actionbar.ActionBarProvider
import com.mincor.noteme.controllers.MainPageController
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.matchParent

class MainActivity : AppCompatActivity(), ActionBarProvider {

    private var mainRouter:Router? = null

    lateinit var mFirebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val container = frameLayout{lparams(matchParent, matchParent)}
        mainRouter = mainRouter?: Conductor.attachRouter(this, container, savedInstanceState)
        if(!mainRouter!!.hasRootController()){
            mainRouter!!.setRoot(RouterTransaction.with(MainPageController()))
        }
    }

    override fun onBackPressed() {
        if (!mainRouter!!.handleBack()) {
            super.onBackPressed()
        }
    }
}
