package com.sahu.playground.data.remte.api

import retrofit2.http.GET

interface Api {

    @GET("data")
    suspend fun getData(): List<Int>
}