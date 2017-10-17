package com.example.cuongcaov.comicbook.main

import java.io.Serializable

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 28/09/2017
 */
data class Comic(val storyId: Long, val storyName: String, val author: String,
                 val intro: String, val chaptersCount: Int, val numberOfChapters: Int,
                 val updateTime: String, val status: String, var readCount: Int, var likeCount: Int,
                 val types: List<Type>, var like: Boolean, var seen: Boolean) : Serializable {
    fun getTypes(): String {
        var str = ""
        types.forEach {
            str += ComicType.getTypeName(it.typeId) + ", "
        }
        return str.substring(0, str.length - 2)
    }
}

data class Type(val typeId: Int) : Serializable
