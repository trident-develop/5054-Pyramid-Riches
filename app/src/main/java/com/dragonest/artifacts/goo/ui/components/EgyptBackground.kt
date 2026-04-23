package com.dragonest.artifacts.goo.ui.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import com.dragonest.artifacts.goo.R
import com.dragonest.artifacts.goo.ui.theme.EgyptGold

/**
 * Full-screen Egyptian background with a soft gold glow that drifts slowly.
 * Use as the root Box of a screen and place content as the children of [content].
 */
@Composable
fun EgyptBackground(
    modifier: Modifier = Modifier,
    dimContent: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        val transition = rememberInfiniteTransition(label = "egypt-bg-glow")
        val glow by transition.animateFloatAsStateCompat(
            from = 0.35f,
            to = 0.7f,
            durationMillis = 3200
        )
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = glow }
                .background(
                    Brush.radialGradient(
                        colors = listOf(EgyptGold.copy(alpha = 0.35f), Color.Transparent),
                    )
                )
        )

        if (dimContent) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.18f))
            )
        }

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter, content = content)
    }
}

/** Tiny helper to keep the call-site readable. */
@Composable
private fun androidx.compose.animation.core.InfiniteTransition.animateFloatAsStateCompat(
    from: Float,
    to: Float,
    durationMillis: Int,
) = animateFloat(
    initialValue = from,
    targetValue = to,
    animationSpec = infiniteRepeatable(
        animation = tween(durationMillis, easing = EaseInOutSine),
        repeatMode = RepeatMode.Reverse
    ),
    label = "egypt-bg-float"
)
