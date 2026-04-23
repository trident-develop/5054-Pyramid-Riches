package com.dragonest.artifacts.goo.ui.screens

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dragonest.artifacts.goo.R
import com.dragonest.artifacts.goo.ui.components.EgyptBackground
import com.dragonest.artifacts.goo.ui.components.MainButton
import com.dragonest.artifacts.goo.ui.components.ScreenTitle
import com.dragonest.artifacts.goo.ui.theme.EgyptGold

@Composable
fun MenuScreen(
    onPlay: () -> Unit,
    onSettings: () -> Unit,
    onLeaderboard: () -> Unit,
    onExit: () -> Unit,
) {
    EgyptBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                GlowIcon(
                    drawable = R.drawable.egypt_el_4,
                    modifier = Modifier.size(140.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                MainButton(text = "Play", onClick = onPlay)
                MainButton(text = "Leaderboard", onClick = onLeaderboard)
                MainButton(text = "Settings", onClick = onSettings)
                MainButton(text = "Exit", onClick = onExit)
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun PulsingMenuTitle() {
    val transition = rememberInfiniteTransition(label = "menu-title")
    val pulse by transition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "menu-title-pulse"
    )
    val glow by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "menu-title-glow"
    )
    Box(contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(width = 340.dp, height = 130.dp)
                .graphicsLayer { alpha = glow * 0.55f }
                .background(
                    Brush.radialGradient(
                        listOf(EgyptGold.copy(alpha = 0.75f), Color.Transparent)
                    )
                )
        )
        ScreenTitle(
            text = "Pyramid Riches",
            fontSize = 38,
            modifier = Modifier.graphicsLayer {
                scaleX = pulse
                scaleY = pulse
            }
        )
    }
}

@Composable
private fun GlowIcon(
    drawable: Int,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "glow-icon")
    val scale by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow-icon-scale"
    )
    val rotation by transition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow-icon-rot"
    )
    Box(contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(190.dp)
                .background(
                    Brush.radialGradient(
                        listOf(EgyptGold.copy(alpha = 0.35f), Color.Transparent)
                    )
                )
        )
        Image(
            painter = painterResource(drawable),
            contentDescription = null,
            modifier = modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation
            }
        )
    }
}
