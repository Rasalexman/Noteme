package com.mincor.noteme.application

import android.app.Application
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.android.autoAndroidModule
import com.github.salomonbrys.kodein.lazy
import com.mincor.noteme.mvp.presenters.addNotePageModule
import com.mincor.noteme.mvp.presenters.mainPageModule
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import com.google.firebase.analytics.FirebaseAnalytics



/**
 * Created by alexander on 01.11.17.
 */
class NotemeApplication : Application(), KodeinAware {

    override val kodein by Kodein.lazy {
        import(autoAndroidModule(this@NotemeApplication))
        import(mainPageModule)
        import(addNotePageModule)
    }

    override fun onCreate() {
        super.onCreate()
        // Obtain the FirebaseAnalytics instance.
        FlowManager.init(FlowConfig.Builder(this).openDatabasesOnInit(true).build())
    }
}