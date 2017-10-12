package com.example.cuongcaov.comicbook.networking

import com.example.cuongcaov.comicbook.main.Comic
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
