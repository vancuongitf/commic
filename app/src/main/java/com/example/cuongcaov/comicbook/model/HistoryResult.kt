package com.example.cuongcaov.comicbook.model

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 20/10/2017
 */
data class HistoryResult(val user: User, val favorite: Set<Long>)

data class User(var name: String, var macAddress: String, var avatar: String)
