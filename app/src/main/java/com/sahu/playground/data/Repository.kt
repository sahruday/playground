package com.sahu.playground.data

import com.sahu.playground.data.cache.LocalService
import com.sahu.playground.data.remte.RemoteService
import com.sahu.playground.stories.StoriesResponse
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val remote: RemoteService,
//    private val local: LocalService,
) {

    suspend fun getData(): StoriesResponse = remote.getData()

    suspend fun getDataFromApi() = flow {
        emit(remote.getData())
    }

}