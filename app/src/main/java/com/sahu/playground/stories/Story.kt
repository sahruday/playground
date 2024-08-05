package com.sahu.playground.stories

import com.google.gson.annotations.SerializedName

data class StoriesResponse(
    val statusCode: Int,
    val message: String,
    val data: List<UserStory>
)

data class UserStory(
    val id: String,
    val name: String,
    val stories: List<Story>
) {
    fun isViewed(): Boolean = stories.all { it.isViewed }
}

data class Story(
    val id: String,
    @SerializedName("imgUrl", alternate = ["url"])
    val imgUrl: String,
    var isViewed: Boolean = false
)
