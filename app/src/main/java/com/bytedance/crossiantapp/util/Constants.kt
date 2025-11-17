package com.bytedance.crossiantapp.util

/**
 * Application-wide constants
 */
object Constants {
    const val BASE_URL = "https://college-training-camp.bytedance.com/"

    // Network
    const val CONNECT_TIMEOUT = 10000L
    const val READ_TIMEOUT = 30000L
    const val WRITE_TIMEOUT = 30000L

    // Paging
    const val PAGE_SIZE = 20

    // MMKV Keys
    const val KEY_LIKED_POSTS = "liked_posts"
    const val KEY_FOLLOWED_USERS = "followed_users"
}