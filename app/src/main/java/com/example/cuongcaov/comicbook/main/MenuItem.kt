package com.example.cuongcaov.comicbook.main

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 01/10/2017
 */
class MenuItem private constructor(val position: Int, val typeId: Int, val typeName: String, var isSelected: Boolean) {
    companion object {
        fun getMenuList(): MutableList<MenuItem> {
            val result = mutableListOf<MenuItem>()
            ComicType.values().withIndex().forEach {
                result.add(MenuItem(it.index, it.value.typeId, it.value.typeName, false))
            }
            return result
        }
    }
}