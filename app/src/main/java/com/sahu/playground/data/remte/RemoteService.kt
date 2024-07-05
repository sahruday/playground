package com.sahu.playground.data.remte

import com.sahu.playground.data.remte.api.Api
import javax.inject.Inject

class RemoteService @Inject constructor(
    private val api: Api,
) {

    suspend fun getData() = api.getData()
}