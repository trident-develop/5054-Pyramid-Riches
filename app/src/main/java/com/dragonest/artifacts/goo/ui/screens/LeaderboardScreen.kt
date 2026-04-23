package com.dragonest.artifacts.goo.ui.screens

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.dragonest.artifacts.goo.R
import com.dragonest.artifacts.goo.data.PreferencesManager
import com.dragonest.artifacts.goo.ui.components.EgyptBackground
import com.dragonest.artifacts.goo.ui.components.IconImageButton
import com.dragonest.artifacts.goo.ui.components.ScreenTitle
import com.dragonest.artifacts.goo.ui.theme.EgyptFontFamily
import com.dragonest.artifacts.goo.ui.theme.EgyptGold
import com.dragonest.artifacts.goo.ui.theme.EgyptGoldDeep
import com.dragonest.artifacts.goo.ui.theme.EgyptSand
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class LeaderboardEntry(
    val name: String,
    val score: Int,
    val isPlayer: Boolean = false,
)

/** Egyptian-flavoured opponent pool. A fixed seed keeps them stable across launches. */
private val FAKE_NAMES = listOf(
    "Ramses", "Cleo", "Khufu", "Nefertiti", "Anubis",
    "Osiris", "Isis", "Horus", "Tutankh", "Akhenaten",
    "Nefertari", "Thutmose", "Hatshepsut", "Sobek", "Bastet",
    "Amun", "Seth", "Thoth", "Ptah", "Sekhmet",
    "Imhotep", "Khonsu", "Merit", "Nefer", "Senusret",
)

@Composable
fun LeaderboardScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager.get(context) }

    val playerTotal = prefs.totalPlayerScore()

    val entries = remember(playerTotal) {
        val r = Random(42)
        val fake = FAKE_NAMES.shuffled(r).take(20).map { name ->
            LeaderboardEntry(name = name, score = r.nextInt(220, 3200))
        }
        (fake + LeaderboardEntry(name = "You", score = playerTotal, isPlayer = true))
            .sortedByDescending { it.score }
    }

    EgyptBackground {
        // Ambient gold dust floating up across the whole screen behind the panel.
        GoldDustLayer()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .padding(top = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconImageButton(
                    drawableRes = R.drawable.back_button,
                    onClick = onBack,
                    size = 52.dp,
                    contentDescription = "Back",
                )
                ScreenTitle(text = "Leaderboard", fontSize = 26)
                Spacer(Modifier.size(52.dp))
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                EgyptSand,
                                Color(0xFFCFA369),
                            )
                        )
                    )
                    .border(
                        width = 3.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(EgyptGold, EgyptGoldDeep)
                        ),
                        shape = RoundedCornerShape(20.dp),
                    ),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 18.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(entries) { entry ->
                        LeaderboardRow(
                            rank = entries.indexOf(entry) + 1,
                            entry = entry,
                        )
                    }
                }
                // Diagonal light sheen sliding across the panel.
                PanelSheen()
            }
        }
    }
}

/* ----------------------------- Row ----------------------------- */

@Composable
private fun LeaderboardRow(
    rank: Int,
    entry: LeaderboardEntry,
) {
    val rowBg = if (entry.isPlayer) EgyptGold.copy(alpha = 0.28f) else Color.Transparent
    val accent = if (entry.isPlayer) Color(0xFF2196F3) else Color(0xFF442A02)
    val weight = if (entry.isPlayer) FontWeight.Bold else FontWeight.SemiBold

    Box(modifier = Modifier.fillMaxWidth()) {
        if (entry.isPlayer) {
            PlayerRowHalo(modifier = Modifier.matchParentSize())
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(rowBg)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (rank in 1..3) {
                RankMedallion(
                    rank = rank,
                    modifier = Modifier
                        .size(34.dp)
                        .padding(end = 6.dp),
                )
            } else {
                Text(
                    text = rank.toString().padStart(2, ' '),
                    style = TextStyle(
                        fontFamily = EgyptFontFamily,
                        fontSize = 18.sp,
                        color = accent,
                        fontWeight = weight,
                        textAlign = TextAlign.Start,
                    ),
                    modifier = Modifier.width(34.dp),
                )
            }
            Text(
                text = entry.name,
                style = TextStyle(
                    fontFamily = EgyptFontFamily,
                    fontSize = 18.sp,
                    color = accent,
                    fontWeight = weight,
                ),
                modifier = Modifier.weight(1f),
            )
            Text(
                text = entry.score.toString(),
                style = TextStyle(
                    fontFamily = EgyptFontFamily,
                    fontSize = 18.sp,
                    color = accent,
                    fontWeight = weight,
                    textAlign = TextAlign.End,
                ),
            )
        }
    }
}

/* ----------------------------- Medallions ----------------------------- */

private data class MedalPalette(
    val bright: Color,
    val deep: Color,
    val ring: Color,
    val text: Color,
)

private fun paletteFor(rank: Int): MedalPalette = when (rank) {
    1 -> MedalPalette( // gold
        bright = Color(0xFFFFE68A),
        deep = Color(0xFFB8862A),
        ring = Color(0xFFFFF4BF),
        text = Color(0xFF3A1F00),
    )
    2 -> MedalPalette( // silver
        bright = Color(0xFFF4F4F4),
        deep = Color(0xFF8C8C95),
        ring = Color(0xFFFFFFFF),
        text = Color(0xFF2A2A2E),
    )
    else -> MedalPalette( // bronze
        bright = Color(0xFFE4A271),
        deep = Color(0xFF7A3E14),
        ring = Color(0xFFF6C99D),
        text = Color(0xFF3A1400),
    )
}

@Composable
private fun RankMedallion(
    rank: Int,
    modifier: Modifier = Modifier,
) {
    val palette = paletteFor(rank)
    val transition = rememberInfiniteTransition(label = "medal-$rank")
    val rayRotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000 + rank * 1500, easing = LinearEasing),
        ),
        label = "medal-rays-$rank"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "medal-pulse-$rank"
    )
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = pulse
                    scaleY = pulse
                },
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val outerR = size.minDimension / 2f

            // Rotating rays behind the medal.
            rotate(degrees = rayRotation, pivot = Offset(cx, cy)) {
                val rayCount = 10
                for (i in 0 until rayCount) {
                    val a = (i * 2f * PI / rayCount).toFloat()
                    val x1 = cx + cos(a) * outerR * 0.62f
                    val y1 = cy + sin(a) * outerR * 0.62f
                    val x2 = cx + cos(a) * outerR * 1.05f
                    val y2 = cy + sin(a) * outerR * 1.05f
                    drawLine(
                        color = palette.bright.copy(alpha = 0.55f),
                        start = Offset(x1, y1),
                        end = Offset(x2, y2),
                        strokeWidth = 2.2f,
                    )
                }
            }
            // Coin body.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(palette.bright, palette.deep),
                    center = Offset(cx - outerR * 0.25f, cy - outerR * 0.25f),
                    radius = outerR * 1.1f,
                ),
                radius = outerR * 0.68f,
                center = Offset(cx, cy),
            )
            // Outer ring.
            drawCircle(
                color = palette.ring,
                radius = outerR * 0.68f,
                center = Offset(cx, cy),
                style = Stroke(width = 2f),
            )
            // Specular highlight on top-left.
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = outerR * 0.12f,
                center = Offset(cx - outerR * 0.22f, cy - outerR * 0.22f),
            )
        }
        Text(
            text = rank.toString(),
            style = TextStyle(
                fontFamily = EgyptFontFamily,
                fontSize = 15.sp,
                color = palette.text,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

/* ----------------------------- Halos & sheen ----------------------------- */

@Composable
private fun PlayerRowHalo(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "player-halo")
    val alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "player-halo-alpha"
    )
    Box(
        modifier = modifier
            .graphicsLayer { this.alpha = alpha }
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        EgyptGold.copy(alpha = 0.55f),
                        Color.Transparent,
                    ),
                )
            )
    )
}

@Composable
private fun PanelSheen() {
    val transition = rememberInfiniteTransition(label = "panel-sheen")
    val sweep by transition.animateFloat(
        initialValue = -0.6f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            initialStartOffset = StartOffset(offsetMillis = 1200),
        ),
        label = "panel-sheen-sweep"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationZ = -18f
                translationX = sweep * size.width
                alpha = 0.28f
            }
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.65f),
                        Color.Transparent,
                    )
                )
            )
    )
}

@Composable
private fun GoldDustLayer() {
    val particles = remember {
        val r = Random(9001)
        List(18) {
            DustParticle(
                xFrac = r.nextFloat(),
                delayMs = r.nextInt(0, 5000),
                durationMs = r.nextInt(5500, 9500),
                radius = r.nextFloat() * 3f + 2f,
                phase = r.nextFloat() * 2f,
                amplitudePx = r.nextFloat() * 28f + 10f,
            )
        }
    }
    val transition = rememberInfiniteTransition(label = "gold-dust")
    particles.forEachIndexed { idx, p ->
        val progress by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(p.durationMs, easing = LinearEasing),
                initialStartOffset = StartOffset(offsetMillis = p.delayMs),
            ),
            label = "gold-dust-$idx"
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val y = size.height * (1f - progress)
            val x = size.width * p.xFrac +
                    (sin((progress * 2f + p.phase) * PI)).toFloat() * p.amplitudePx
            val alpha = (sin(progress * PI)).toFloat().coerceIn(0f, 1f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF5C8).copy(alpha = alpha),
                        EgyptGold.copy(alpha = alpha * 0.55f),
                        Color.Transparent,
                    ),
                    center = Offset(x, y),
                    radius = p.radius * 4f,
                ),
                radius = p.radius * 4f,
                center = Offset(x, y),
            )
            drawCircle(
                color = Color(0xFFFFF5C8).copy(alpha = alpha),
                radius = p.radius,
                center = Offset(x, y),
            )
        }
    }
}

private data class DustParticle(
    val xFrac: Float,
    val delayMs: Int,
    val durationMs: Int,
    val radius: Float,
    val phase: Float,
    val amplitudePx: Float,
)

/* ----------------------------- Previews ----------------------------- */