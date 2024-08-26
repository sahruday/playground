package com.sahu.playground.stories

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sahu.playground.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class StoriesVM @Inject constructor(
    private val repo : Repository
) : ViewModel() {

    sealed interface State
    data object LOADING : State
    class SUCCESS(val stories: List<UserStory>) : State
    class ERROR(val message: String) : State

    private val _state = MutableStateFlow<State>(LOADING)
    val state = _state.asStateFlow()

    val detailedUserStoryIndex = MutableStateFlow(-1)
    val detailedStoryIndex = MutableStateFlow(0)

    init {
        getStories()
    }

    fun getStories() {
        viewModelScope.launch {

            _state.value = LOADING
            delay(1000)

            try {
                val successResponse = repo.getData()
                _state.value = if(successResponse.statusCode == 200) {
                    SUCCESS(successResponse.data)
                } else {
                    ERROR(successResponse.message)
                }
            } catch (e: Exception) {
                _state.value = ERROR(e.message ?: "Something went wrong")
            }

//            val gson = Gson()
//            println(successResponse)
//            val storiesResponse = gson.fromJson(successResponse, StoriesResponse::class.java)
//            _state.value = SUCCESS(storiesResponse.data)
        }
    }

    fun nextStory(currentUserStoryIndex: Int) {
        val userStories = (state.value as SUCCESS).stories
        val lastIndex = userStories[currentUserStoryIndex].stories.lastIndex
        if(detailedStoryIndex.value in 0 until lastIndex)
            detailedStoryIndex.value++
        else if( detailedStoryIndex.value == lastIndex)
            if(detailedUserStoryIndex.value < userStories.lastIndex) nextUser(currentUserStoryIndex)
            else detailedUserStoryIndex.value = -1 //Close
        Log.d("TAG", "nextStory: ${detailedUserStoryIndex.value}${detailedStoryIndex.value}")
    }

    fun nextUser(currentUserStoryIndex: Int) {
        val userStories = (state.value as SUCCESS).stories
        if(currentUserStoryIndex < userStories.lastIndex) {
            detailedUserStoryIndex.value = currentUserStoryIndex+1
            detailedStoryIndex.value = 0
        }
    }

    fun prevStory(currentUserStoryIndex: Int) {
        if(detailedStoryIndex.value > 0) detailedStoryIndex.value--
        else if(detailedStoryIndex.value == 0) prevUser(currentUserStoryIndex)
    }

    fun prevUser(currentUserStoryIndex: Int) {
        if(currentUserStoryIndex > 0) detailedUserStoryIndex.value = currentUserStoryIndex-1
    }

    companion object {
        val successResponse = JSONObject().apply {
            put("data", JSONArray().apply {
                put(JSONObject().apply {
                    put("id", 1)
                    put("name", "User 1")
                    put("stories", JSONArray().apply {
                        put(JSONObject().apply {
                            put("id", 101)
                            put("url", "https://picsum.photos/id/0/5000/3333")
                        })
                        put(JSONObject().apply {
                            put("id", 102)
                            put("url", "https://picsum.photos/id/1/5000/3333")
                        })
                        put(JSONObject().apply {
                            put("id", 103)
                            put("url", "https://picsum.photos/id/2/5000/3333")
                        })
                    })
                })

                put(JSONObject().apply {
                    put("id", 2)
                    put("name", "User 2")
                    put("stories", JSONArray().apply {
                        put(JSONObject().apply {
                            put("id", 201)
                            put("url", "https://picsum.photos/id/7/4728/3168")
                        })
                        put(JSONObject().apply {
                            put("id", 202)
                            put("url", "https://picsum.photos/id/8/5000/3333")
                        })
                        put(JSONObject().apply {
                            put("id", 203)
                            put("url", "https://picsum.photos/id/9/5000/3269")
                        })
                        put(JSONObject().apply {
                            put("id", 204)
                            put("url", "https://picsum.photos/id/10/2500/1667")
                        })
                    })
                })

                put(JSONObject().apply {
                    put("id", 3)
                    put("name", "User 3")
                    put("stories", JSONArray().apply {
                        put(JSONObject().apply {
                            put("id", 301)
                            put("url", "https://picsum.photos/id/15/2500/1667")
                        })
                        put(JSONObject().apply {
                            put("id", 302)
                            put("url", "https://picsum.photos/id/16/2500/1667")
                        })
                    })
                })

                put(JSONObject().apply {
                    put("id", 4)
                    put("name", "User 4")
                    put("stories", JSONArray().apply {
                        put(JSONObject().apply {
                            put("id", 401)
                            put("url", "https://picsum.photos/id/17/2500/1667")
                        })
                    })
                })



            })
        }//.toString()

    }
}