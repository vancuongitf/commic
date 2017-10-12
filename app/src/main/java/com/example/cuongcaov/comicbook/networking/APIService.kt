package com.example.cuongcaov.comicbook.networking

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 28/09/2017
 */
interface APIService {

    @GET("api-stories.php")
    fun getStories(@Query("page") page: Int, @Query("query") query: String): Call<APIResult>

    @GET("api-storiesbytype.php")
    fun getStoriesByType(@Query("page") page: Int, @Query("typeId") typeId: Int): Call<APIResult>

    @GET("api-chapters.php")
    fun getChapterList(@Query("storyId") storyId: Long, @Query("page") page: Int): Call<APIResultChapter>

    @GET("api-contents.php")
    fun getContents(@Query("chapterId") chapterId: Long): Call<List<Content>>

    @GET("api-getcontents.php")
    fun getContentList(@Query("chapterId") chapterId: Long): Call<ChapterContents>

}