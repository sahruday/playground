package com.sahu.playground.data.remte

import com.sahu.playground.data.remte.api.Api
import com.sahu.playground.stories.StoriesResponse
import javax.inject.Inject

class RemoteService @Inject constructor(
    private val api: Api,
) {

    suspend fun getData(): StoriesResponse = api.getData()
}