package com.sahu.playground.stories

import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.LocalImageLoader
import coil.compose.rememberAsyncImagePainter
import com.sahu.playground.R
import com.sahu.playground.appUtil.BaseActivity
import com.sahu.playground.commonCompose.StepProgressIndicator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoriesActivity : BaseActivity() {

    companion object {
        const val DEEPLINK_PATH = "stories"

        const val TAG = "StoriesActivity"

        const val PROGRESS_DELAY = 5000
    }

    private val viewModel by viewModels<StoriesVM>()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ComposableView() {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val state = viewModel.state.collectAsState()
            Column(modifier = Modifier.navigationBarsPadding()) {
                LargeTopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = { Text(text = "Instagram") })

                Box(modifier = Modifier.fillMaxSize()) {
                    when (state.value) {
                        is StoriesVM.LOADING -> {
                            ContentLoadingProgressBar(Modifier.align(Alignment.Center))
                        }

                        is StoriesVM.SUCCESS -> {
                            (state.value as StoriesVM.SUCCESS).stories.takeIf { it.isNotEmpty() }
                                ?.let {
                                    HorizontalStoryList(userStories = it)
                                } ?: run {
                                Text(text = "No Stories")
                            }
                        }

                        is StoriesVM.ERROR -> { /*TODO()*/ }
                    }
                }
            }

            viewModel.detailedStoryIndex.collectAsState().value
                .takeIf { it.userStoryIndex >= 0 && state.value is StoriesVM.SUCCESS }
                ?.let {
                    val userStories = (state.value as StoriesVM.SUCCESS).stories
                    FullScreenStory(
                        userStories = userStories,
                        currentUserIndex = it.userStoryIndex,
                        currentStoryIndex = it.storyIndex,
                        modifier = Modifier.systemBarsPadding(),
                        nextStory = {
                            Log.d(TAG, "ComposableView: nextStory")
                            viewModel.nextStory()
                        },
                        prevStory = {
                            Log.d(TAG, "ComposableView: prevStory")
                            viewModel.prevStory()
                        },
                        nextUser = {
                            Log.d(TAG, "ComposableView: nextUser")
                            viewModel.nextUser()
                        },
                        prevUser = {
                            Log.d(TAG, "ComposableView: prevUser")
                            viewModel.prevUser()
                        },
                    )
                }
        }
    }

    @Composable
    fun ContentLoadingProgressBar(modifier: Modifier = Modifier) {
        CircularProgressIndicator(modifier = modifier)
    }

    @Composable
    fun HorizontalStoryList(userStories: List<UserStory>, modifier: Modifier = Modifier) {
        LazyRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(32.dp)
        ) {
            itemsIndexed(userStories.filter { it.stories.isNotEmpty() }) { index, it ->
                Column(modifier = Modifier
                    .width(150.dp)
                    .clickable {
                        viewModel.detailedStoryIndex.value = StoriesVM.CurrentStoryIndex(index)
                    },
                ) {
                    AsyncImage(
                        model = it.stories.first().imgUrl,
                        contentDescription = "${it.name}'s Story",
                        placeholder = rememberAsyncImagePainter(R.drawable.ic_launcher_foreground),
                        error = rememberAsyncImagePainter(android.R.drawable.ic_delete),
                        fallback = rememberAsyncImagePainter(android.R.drawable.btn_plus),
                        onError = {
                            Log.e(TAG, "HorizontalStoryList: ${it.result.throwable}")
                        },
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                    )

                    Text(
                        text = it.name,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }


    @Composable
    fun FullScreenStory(
        userStories: List<UserStory>,
        modifier: Modifier = Modifier,
        currentUserIndex: Int,
        currentStoryIndex: Int,
        nextUser: () -> Unit = {},
        prevUser: () -> Unit = {},
        nextStory: () -> Unit = {},
        prevStory: () -> Unit = {}
    ) {
        val url by remember(currentUserIndex, currentStoryIndex) {
            derivedStateOf {
                userStories[currentUserIndex].stories[currentStoryIndex].imgUrl
            }
        }

        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }

        val context = LocalContext.current
        val imageLoader = remember {
            ImageLoader(context).newBuilder()
                .crossfade(true)
                .build()
        }

        AsyncImage(
            model = url,
            contentDescription = "Detail Story",
            imageLoader = imageLoader,
            contentScale = ContentScale.Inside,
            placeholder = rememberAsyncImagePainter(R.drawable.ic_launcher_foreground),
            error = rememberAsyncImagePainter(android.R.drawable.ic_delete),
            fallback = rememberAsyncImagePainter(android.R.drawable.btn_plus),
            onError = {
                Log.e(TAG, "FullScreenStory: ${it.result.throwable}")
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures {
                        if (it.x > size.width / 2) nextStory()
                        else prevStory()
                    }
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(onDragEnd = {
                        if (offsetX < 0) nextUser()
                        else prevUser()
                        offsetX = 0f
                    }) { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount
                    }
                }
//                .pointerInput(Unit) {
//                    detectVerticalDragGestures(
//                        onDragEnd = {
//                            if(offsetY > 0) onBackPressed()
//                            offsetY = 0f
//                        }
//                    ) { change, dragAmount ->
//                        change.consume()
//                        offsetY += dragAmount
//                    }
//                }
        )

        val title = userStories[currentUserIndex].name
        val itemsCount = userStories[currentUserIndex].stories.size

        Box(modifier = modifier) {
            Column(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Black,
                                Color.Transparent
                            )
                        )
                    )
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.Center,
            ) {
                //Progress Indicator
                AutoLinearStepProgress(
                    url = url,
                    modifier = Modifier.fillMaxWidth(),
                    items = itemsCount,
                    currentStoryIndex = currentStoryIndex,
                    strokeCap = StrokeCap.Round,
                    trackColor = Color.LightGray
                ) {
                    nextStory()
                }

                //Back Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = "Profile Img",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(color = Color.Black)
                            .border(2.dp, Color.White, CircleShape)
                    )

                    Text(
                        text = title,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )

                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                viewModel.detailedStoryIndex.value = StoriesVM.CurrentStoryIndex()
                            }
                            .padding(12.dp)
                    )
                }
            }


        }
    }

    @Composable
    fun AutoLinearStepProgress(
        url: String,
        items: Int,
        currentStoryIndex: Int,
        modifier: Modifier = Modifier,
        strokeCap: StrokeCap = StrokeCap.Round,
        trackColor: Color = Color.LightGray,
        finishedListener: () -> Unit = {}
    ) {
        var progress by remember(currentStoryIndex, currentStoryIndex) { mutableFloatStateOf(0f) }
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(durationMillis = if (progress == 0f) 0 else PROGRESS_DELAY, easing = LinearEasing),
            label = "Step progress animation",
            finishedListener = { if (it == 1f) finishedListener() }
        )

        LaunchedEffect(url) {
            progress = 0f
            progress = 1f
        }

        StepProgressIndicator(
            items = items,
            progress = currentStoryIndex + animatedProgress,
            modifier = modifier,
            strokeCap = strokeCap,
            trackColor = trackColor
        )
    }

    override fun onBackPressed() {
        if (viewModel.detailedStoryIndex.value.userStoryIndex == -1)
            super.onBackPressed()
        else
            viewModel.detailedStoryIndex.value = StoriesVM.CurrentStoryIndex()

    }
}