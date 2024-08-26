package com.sahu.playground.data.remte.api

import com.sahu.playground.stories.StoriesResponse
import retrofit2.http.GET

interface Api {

    @GET("stories")
    suspend fun getData(): StoriesResponse
}