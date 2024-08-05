package com.sahu.playground.stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    data class CurrentStoryIndex(val userStoryIndex: Int = -1, val storyIndex: Int = 0)
    val detailedStoryIndex = MutableStateFlow(CurrentStoryIndex())

    init {
        getStories()
    }

    fun getStories() {
        viewModelScope.launch {

            _state.value = LOADING
            delay(1000)

            val successResponse = repo.getData()
            try {
                _state.value = SUCCESS(successResponse.data)
            } catch (e: Exception) {
                _state.value = ERROR(e.message ?: "Something went wrong")
            }

//            val gson = Gson()
//            println(successResponse)
//            val storiesResponse = gson.fromJson(successResponse, StoriesResponse::class.java)
//            _state.value = SUCCESS(storiesResponse.data)
        }
    }

    fun nextStory() {
        val userStories = (state.value as SUCCESS).stories
        if(detailedStoryIndex.value.storyIndex < userStories[detailedStoryIndex.value.userStoryIndex].stories.lastIndex) {
            detailedStoryIndex.value = detailedStoryIndex.value.copy(storyIndex = detailedStoryIndex.value.storyIndex+1)
        }
        else if(detailedStoryIndex.value.storyIndex == userStories[detailedStoryIndex.value.userStoryIndex].stories.lastIndex) {
            if(detailedStoryIndex.value.userStoryIndex < userStories.lastIndex) nextUser()
            else detailedStoryIndex.value = CurrentStoryIndex()
        }
    }

    fun nextUser() {
        val userStories = (state.value as SUCCESS).stories
        if(detailedStoryIndex.value.userStoryIndex < userStories.lastIndex) {
            detailedStoryIndex.value = CurrentStoryIndex(userStoryIndex = detailedStoryIndex.value.userStoryIndex+1)
        }
    }

    fun prevStory() {
        if(detailedStoryIndex.value.storyIndex > 0) {
            detailedStoryIndex.value = detailedStoryIndex.value.copy(storyIndex = detailedStoryIndex.value.storyIndex-1)
        }
        else if(detailedStoryIndex.value.storyIndex == 0)
            prevUser()
    }

    fun prevUser() {
        if(detailedStoryIndex.value.userStoryIndex > 0) {
            detailedStoryIndex.value = CurrentStoryIndex(userStoryIndex = detailedStoryIndex.value.userStoryIndex-1)
        }
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
        }.toString()

    }
}