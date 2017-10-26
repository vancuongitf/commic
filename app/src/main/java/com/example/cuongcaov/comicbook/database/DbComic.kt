package com.example.cuongcaov.comicbook.database

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.cuongcaov.comicbook.model.History
import com.example.cuongcaov.comicbook.ultis.Constants

@SuppressLint("Registered")
/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 26/10/2017
 */
class DbComic : Activity {
    private lateinit var mDb: SQLiteDatabase
    private lateinit var mOpenHelper: ComicOpenHelper
    private lateinit var mContext: Context

    constructor(context: Context) {
        mContext = context
    }

    fun open(): DbComic {
        mOpenHelper = ComicOpenHelper(mContext)
        mDb = mOpenHelper.writableDatabase
        return this
    }

    fun close() {
        mOpenHelper.close()
    }

    fun insertHistory(history: History): Boolean {
        val contentValue = ContentValues()
        if (isExist(history.storyId)) {
            contentValue.put(Constants.COLUMN_CHAPTER_ID, history.chapterId)
            contentValue.put(Constants.COLUMN_POSITION, history.position)
            return mDb.update(Constants.TABLE_HISTORY, contentValue, "${Constants.COLUMN_STORY_ID} = ${history.storyId}", null) > -1
        }
        contentValue.put(Constants.COLUMN_STORY_ID, history.storyId)
        contentValue.put(Constants.COLUMN_CHAPTER_ID, history.chapterId)
        contentValue.put(Constants.COLUMN_POSITION, history.position)
        return mDb.insert(Constants.TABLE_HISTORY, null, contentValue) > -1
    }

    fun deleteHistory(storyId: Long): Boolean {
        return mDb.delete(Constants.TABLE_HISTORY, " ${Constants.COLUMN_STORY_ID} = $storyId", null) > 0
    }

    fun getHistory(): MutableList<History> {
        val result = mutableListOf<History>()
        val cursor = mDb.rawQuery("SELECT * FROM ${Constants.TABLE_HISTORY} " +
                "ORDER BY ${Constants.COLUMN_TIME_UPDATE} DESC", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            result.add(History(cursor.getLong(0), cursor.getLong(1), cursor.getInt(2)))
            cursor.moveToNext()
        }
        cursor.close()
        return result
    }

    private fun isExist(storyId: Long): Boolean {
        val cursor = mDb.rawQuery("SELECT * FROM ${Constants.TABLE_HISTORY} " +
                "WHERE ${Constants.COLUMN_STORY_ID} = $storyId;", null)
        val rs = cursor.count == 1
        cursor.close()
        return rs
    }
}