package com.mincor.noteme.application

import android.app.Application
import com.dbflow5.config.FlowConfig
import com.dbflow5.config.FlowManager
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.android.autoAndroidModule
import com.github.salomonbrys.kodein.lazy
import com.mincor.noteme.mvp.presenters.addNotePageModule
import com.mincor.noteme.mvp.presenters.mainPageModule


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