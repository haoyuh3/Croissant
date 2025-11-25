package com.bytedance.croissantapp.data.remote

import com.bytedance.croissantapp.data.model.FeedResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API interfaces
 */
interface FeedApi {
    /**
     * 获取Feed流
     * @param count 请求数量
     * @param acceptVideoClip 是否支持下发视频片段（进阶功能）
     */
    @GET("feed/")
    suspend fun getFeed (
        @Query("count") count: Int,
        @Query("accept_video_clip") acceptVideoClip: Boolean = true
    ): FeedResponse


}