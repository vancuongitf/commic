package com.example.cuongcaov.comicbook.networking

import retrofit2.Call
import retrofit2.http.*

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

    @POST("api-like.php")
    @FormUrlEncoded
    fun actionLike(@Field("storyId") storyId: Long, @Field("macAddress") macAddress: String,
                   @Field("like") like: Int = 1): Call<APIResultLike>

    @POST("api-history.php")
    @FormUrlEncoded
    fun getHistory(@Field("macAddress") macAddress: String): Call<Set<Long>>

    @GET("api-read.php")
    fun read(@Query("storyId") storyId: Long): Call<Int>

    @POST("api-getsavedhistory.php")
    @FormUrlEncoded
    fun getSavedHistory(@Field("list") list: String): Call<APIResult>
}