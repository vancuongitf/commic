package com.example.cuongcaov.comicbook.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.cuongcaov.comicbook.ultis.Constants

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 26/10/2017
 */
class ComicOpenHelper(context: Context) : SQLiteOpenHelper(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
                "CREATE TABLE ${Constants.TABLE_HISTORY} (" +
                        "${Constants.COLUMN_STORY_ID} LONG PRIMARY KEY, " +
                        "${Constants.COLUMN_CHAPTER_ID} LONG, " +
                        "${Constants.COLUMN_POSITION} INTEGER, " +
                        "${Constants.COLUMN_TIME_UPDATE} LONG);"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXITS ${Constants.TABLE_HISTORY};")
        onCreate(db)
    }
}