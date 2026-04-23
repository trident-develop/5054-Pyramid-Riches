package com.dragonest.artifacts.goo.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.dragonest.artifacts.goo.R
import com.dragonest.artifacts.goo.ui.components.EgyptBackground
import com.dragonest.artifacts.goo.ui.theme.EgyptFontFamily
import com.dragonest.artifacts.goo.ui.theme.EgyptGold
import com.dragonest.artifacts.goo.ui.theme.EgyptGoldDeep
import com.dragonest.artifacts.goo.ui.theme.EgyptShadow
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    BackHandler(enabled = true) {}
    EgyptBackground(modifier = modifier.fillMaxSize(), dimContent = true) {
        PulsingRings()
        FloatingSparkles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            ShimmerTitle()

            OrbitingSymbolsStack()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LoadingDotsText()
                ElegantProgressBar()
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

/* ---------- Title ---------- */

@Composable
private fun ShimmerTitle() {
    val transition = rememberInfiniteTransition(label = "title")
    val scale by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "title-scale"
    )
    val glow by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "title-glow"
    )
    val shimmer by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
        ),
        label = "title-shimmer"
    )
    val shimmerSpan = 420f
    val shimmerBrush = Brush.horizontalGradient(
        colors = listOf(
            EgyptGoldDeep,
            EgyptGold,
            Color(0xFFFFF6C8),
            EgyptGold,
            EgyptGoldDeep,
        ),
        startX = shimmer * shimmerSpan,
        endX = shimmer * shimmerSpan + shimmerSpan,
    )
    Box(contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(width = 360.dp, height = 150.dp)
                .graphicsLayer { alpha = glow * 0.6f }
                .background(
                    Brush.radialGradient(
                        colors = listOf(EgyptGold.copy(alpha = 0.75f), Color.Transparent)
                    )
                )
        )
        Text(
            text = "Pyramid Riches".uppercase(),
            style = TextStyle(
                fontFamily = EgyptFontFamily,
                fontSize = 40.sp,
                brush = shimmerBrush,
                textAlign = TextAlign.Center,
                shadow = Shadow(
                    color = EgyptShadow,
                    offset = Offset(2f, 4f),
                    blurRadius = 8f,
                ),
            ),
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
        )
    }
}

/* ---------- Centerpiece: sun-disk + orbiting symbols ---------- */

@Composable
private fun OrbitingSymbolsStack() {
    val transition = rememberInfiniteTransition(label = "orbit")

    val orbitAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(9500, easing = LinearEasing),
        ),
        label = "orbit-angle"
    )
    val centerScale by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "center-scale"
    )
    val centerRotation by transition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "center-rot"
    )

    val orbitSymbols = listOf(
        R.drawable.egypt_el_2,
        R.drawable.egypt_el_3,
        R.drawable.egypt_el_5,
        R.drawable.egypt_el_6,
        R.drawable.egypt_el_7,
    )
    val orbitRadiusDp = 100.dp
    val orbitRadiusPx = with(LocalDensity.current) { orbitRadiusDp.toPx() }

    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center,
    ) {
        SunDiskBackdrop()

        orbitSymbols.forEachIndexed { i, drawable ->
            val angleDeg = orbitAngle + (360f / orbitSymbols.size) * i
            val rad = Math.toRadians(angleDeg.toDouble())
            val tx = (orbitRadiusPx * cos(rad)).toFloat()
            val ty = (orbitRadiusPx * sin(rad)).toFloat()
            Image(
                painter = painterResource(drawable),
                contentDescription = null,
                modifier = Modifier
                    .size(46.dp)
                    .graphicsLayer {
                        translationX = tx
                        translationY = ty
                        alpha = (0.7f + 0.3f * sin(rad).toFloat()).coerceIn(0.35f, 1f)
                        val scaleWave = 0.85f + 0.2f * sin(rad).toFloat()
                        scaleX = scaleWave
                        scaleY = scaleWave
                    }
            )
        }

        Box(
            modifier = Modifier
                .size(220.dp)
                .graphicsLayer { alpha = 0.55f }
                .background(
                    Brush.radialGradient(
                        colors = listOf(EgyptGold.copy(alpha = 0.55f), Color.Transparent)
                    )
                )
        )

        Image(
            painter = painterResource(R.drawable.egypt_el_1),
            contentDescription = null,
            modifier = Modifier
                .size(130.dp)
                .graphicsLayer {
                    scaleX = centerScale
                    scaleY = centerScale
                    rotationZ = centerRotation
                }
        )
    }
}

@Composable
private fun SunDiskBackdrop() {
    val transition = rememberInfiniteTransition(label = "sun")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
        ),
        label = "sun-rot"
    )
    val rayPulse by transition.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ray-pulse"
    )
    Canvas(
        modifier = Modifier
            .size(260.dp)
            .graphicsLayer { rotationZ = rotation }
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val baseR = size.minDimension * 0.3f
        val rayCount = 12
        for (i in 0 until rayCount) {
            val a = (i * 2 * PI / rayCount)
            val innerR = baseR * 0.9f
            val outerR = baseR * 1.55f
            val x1 = centerX + (cos(a) * innerR).toFloat()
            val y1 = centerY + (sin(a) * innerR).toFloat()
            val x2 = centerX + (cos(a) * outerR).toFloat()
            val y2 = centerY + (sin(a) * outerR).toFloat()
            drawLine(
                color = EgyptGold.copy(alpha = rayPulse),
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                strokeWidth = 4f,
            )
        }
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(EgyptGold.copy(alpha = 0.55f), Color.Transparent),
                center = Offset(centerX, centerY),
                radius = baseR * 1.6f,
            ),
            radius = baseR * 1.6f,
            center = Offset(centerX, centerY),
        )
    }
}

/* ---------- Background layers ---------- */

@Composable
private fun PulsingRings() {
    val transition = rememberInfiniteTransition(label = "rings")
    val ringCount = 3
    val durationMs = 3400
    for (i in 0 until ringCount) {
        val phase by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMs, easing = LinearEasing),
                initialStartOffset = StartOffset(offsetMillis = i * (durationMs / ringCount)),
            ),
            label = "ring-$i"
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.46f)
            val maxR = size.minDimension * 0.75f
            val r = lerp(60f, maxR, phase)
            val alpha = (1f - phase).coerceIn(0f, 1f) * 0.55f
            drawCircle(
                color = EgyptGold.copy(alpha = alpha),
                radius = r,
                center = center,
                style = Stroke(width = 3f),
            )
        }
    }
}

private data class Sparkle(
    val xFrac: Float,
    val delayMs: Int,
    val durationMs: Int,
    val radius: Float,
    val wavePhase: Float,
    val waveAmpPx: Float,
)

@Composable
private fun FloatingSparkles(count: Int = 26) {
    val sparkles = remember {
        val r = Random(1337)
        List(count) {
            Sparkle(
                xFrac = r.nextFloat(),
                delayMs = r.nextInt(0, 4500),
                durationMs = r.nextInt(4000, 7000),
                radius = r.nextFloat() * 3.5f + 2.5f,
                wavePhase = r.nextFloat() * 2f,
                waveAmpPx = r.nextFloat() * 22f + 8f,
            )
        }
    }
    val transition = rememberInfiniteTransition(label = "sparkles")
    sparkles.forEachIndexed { idx, sp ->
        val progress by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(sp.durationMs, easing = LinearEasing),
                initialStartOffset = StartOffset(offsetMillis = sp.delayMs),
            ),
            label = "sparkle-$idx"
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val y = size.height * (1f - progress)
            val xBase = size.width * sp.xFrac
            val xWave = (sin((progress * 2f + sp.wavePhase) * PI)).toFloat() * sp.waveAmpPx
            // Fade in and out across lifetime.
            val alpha = (sin(progress * PI)).toFloat().coerceIn(0f, 1f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF5C8).copy(alpha = alpha),
                        EgyptGold.copy(alpha = alpha * 0.6f),
                        Color.Transparent,
                    ),
                    center = Offset(xBase + xWave, y),
                    radius = sp.radius * 4f,
                ),
                radius = sp.radius * 4f,
                center = Offset(xBase + xWave, y),
            )
            drawCircle(
                color = Color(0xFFFFF5C8).copy(alpha = alpha),
                radius = sp.radius,
                center = Offset(xBase + xWave, y),
            )
        }
    }
}

/* ---------- Loading text + progress ---------- */

@Composable
private fun LoadingDotsText() {
    val transition = rememberInfiniteTransition(label = "dots")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
        ),
        label = "dots-phase"
    )
    val dots = phase.toInt().coerceIn(0, 3)
    Text(
        text = "Loading" + ".".repeat(dots),
        style = TextStyle(
            fontFamily = EgyptFontFamily,
            fontSize = 22.sp,
            color = EgyptGold,
            textAlign = TextAlign.Center,
            shadow = Shadow(EgyptShadow, Offset(1f, 2f), 4f),
        )
    )
}

@Composable
private fun ElegantProgressBar() {
    val transition = rememberInfiniteTransition(label = "progress")
    val shimmer by transition.animateFloat(
        initialValue = -0.35f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
        ),
        label = "progress-shimmer"
    )
    val glow by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "progress-glow"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp)
            .height(18.dp)
            .clip(RoundedCornerShape(9.dp))
            .border(1.dp, EgyptGold.copy(alpha = 0.85f), RoundedCornerShape(9.dp))
            .background(Color(0x90120906))
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(EgyptGoldDeep, EgyptGold, EgyptGoldDeep)
                    )
                )
                .graphicsLayer { alpha = glow * 0.65f }
        )
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer { translationX = shimmer * this.size.width }
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.7f),
                            Color.Transparent,
                        ),
                    )
                )
        )
    }
}
