package com.example.cuongcaov.comicbook.model

import java.io.Serializable

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 28/09/2017
 */
data class APIResult(val count: Int, val data: List<Comic>)

data class APIResultChapter(val chapterCount: Int, val data: List<Chapter>)

data class Chapter(val chapterId: Long, val position: Int) : Serializable

data class Content(val contentId: Long, val chapterId: Long, val position: Int, val source: String)

data class ChapterContents(val nextChapter: Long, val previousChapter: Long, val contentList: List<Content>)

data class APIResultLike(val status: Boolean, val count: Int)

data class AvatarResult(val path: String)

data class NameResult(val status: Boolean)

data class Comment(val macAddress: String, val userName: String, val avatar: String,
                   val comment: String, val time: String)

data class StatusResult(val status: Boolean)
