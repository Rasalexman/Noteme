package com.mincor.noteme.mvp.models

import com.mincor.noteme.common.NotemeDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.io.Serializable
import java.util.*

/**
 * Created by alexander on 07.11.17.
 */
@Table(database = NotemeDatabase::class)
data class NoteModel(@PrimaryKey(autoincrement = true) @Column(name = "id") var id: Int = 0,
                     @Column var title: String? = null,
                     @Column var text:String? = null,
                     @Column var createDate: Date? = null,
                     @Column var changeDate: Date? = null
                     ):Serializable