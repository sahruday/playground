package com.sahu.playground.animations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp

@Composable
fun ProgressTextButton(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    shape: Shape = ButtonDefaults.shape,
    initialColor: Color = MaterialTheme.colorScheme.primary,
    finalColor: Color = Color.Black,
    initialContentColor: Color = Color.Black,
    finalContentColor: Color = MaterialTheme.colorScheme.onSurface,
    contentPaddingValues: PaddingValues = ButtonDefaults.ContentPadding,
    duration: Int = 5000,
    placeholders: List<AnnotatedString.Range<Placeholder>> = emptyList(),
    finishedListener: ((progress: Float) -> Unit)? = null,
    onClick: () -> Unit,
) {
    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = if(progress == 0f) 0 else duration, easing = LinearEasing),
        label = "Progress Text Animator",
        finishedListener = finishedListener
    )

    LaunchedEffect(Unit) { progress = 1f }

    Surface(
        color = Color.Transparent,
        shape = shape,
        modifier = modifier
            .border(2.dp, finalColor, shape)
            .background(
                color = initialColor,
                shape = shape
            )
            .clip(shape)
            .clickable { onClick.invoke() }
            .drawWithContent {
                clipRect(
                    left = 0f,
                    top = 0f,
                    right = size.width * animatedProgress,
                    bottom = size.height
                ) {
                    drawRect(
                        color = finalColor
                    )
                }
                drawContent()
            }
    ) {
        CompositionLocalProvider(LocalContentColor provides initialColor) {
            ProvideTextStyle(value = MaterialTheme.typography.headlineLarge) {
                ProgressText(
                    progress = animatedProgress,
                    text = text,
                    contentPaddingValues = contentPaddingValues,
                    initialColor = initialContentColor,
                    finalColor = finalContentColor,
                    placeholders = placeholders
                )
            }
        }
    }
}

@Composable
fun ProgressTextView(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    duration: Int = 3000
) {
    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = if(progress == 0f) 0 else duration, easing = LinearEasing),
        label = "" // Customize duration
    )

    LaunchedEffect(Unit) { progress = 1f }

    ProgressText(progress = animatedProgress, text = text, modifier = modifier)
}

@Composable
fun ProgressText(
    progress: Float,
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    placeholders: List<AnnotatedString.Range<Placeholder>> = emptyList(),
    contentPaddingValues: PaddingValues = PaddingValues(0.dp),
    initialColor: Color = Color.Black,
    finalColor: Color = Color.White
) {
    val textStyle = LocalTextStyle.current
    val textMeasurer = rememberTextMeasurer()
    val localLayoutDirection = LocalLayoutDirection.current
    Text(
        text = text,
        style = LocalTextStyle.current.copy(color = initialColor),
        modifier = modifier
            .drawWithContent {
                drawContent()
                clipRect(
                    left = 0f,
                    top = 0f,
                    right = size.width * progress,
                    bottom = size.height
                ) {
                    drawText(
                        text = text,
                        placeholders = placeholders,
                        textMeasurer = textMeasurer,
                        topLeft = Offset(
                            contentPaddingValues.calculateLeftPadding(localLayoutDirection).toPx(),
                            contentPaddingValues.calculateTopPadding().toPx()
                        ),
                        style = textStyle.copy(color = finalColor),
                    )
                }
            }
            .padding(contentPaddingValues)
    )
}