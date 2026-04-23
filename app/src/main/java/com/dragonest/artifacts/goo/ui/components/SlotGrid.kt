package com.dragonest.artifacts.goo.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.dragonest.artifacts.goo.game.MatchedCell
import com.dragonest.artifacts.goo.game.SlotBoard
import com.dragonest.artifacts.goo.ui.theme.EgyptGold
import com.dragonest.artifacts.goo.ui.theme.EgyptGoldDeep
import com.dragonest.artifacts.goo.ui.theme.EgyptOverlay
import com.dragonest.artifacts.goo.ui.theme.EgyptSand
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Slot grid (4 columns x 5 rows) with staggered column scroll animation.
 *
 * Usage:
 *   val state = rememberSlotGridState(initial)
 *   SlotGrid(state, matchedCells = ...)
 *   // Inside a coroutine: state.spinTo(targetGrid)
 */
class SlotGridState internal constructor(
    initialGrid: Array<IntArray>,
) {
    var currentGrid: Array<IntArray> = initialGrid
        private set

    private val pauseFlow = MutableStateFlow(false)

    internal val reels: List<SlotReelState> = List(SlotBoard.COLS) { col ->
        SlotReelState(
            initialColumn = IntArray(SlotBoard.ROWS) { initialGrid[col][it] },
            pauseFlow = pauseFlow,
        )
    }

    /** Freeze any in-flight spin. Reels hold their current offset until resumed. */
    fun setPaused(paused: Boolean) {
        pauseFlow.value = paused
    }

    /**
     * Animate every column toward [targetGrid] with staggered durations.
     * Suspends until every column has settled.
     */
    suspend fun spinTo(targetGrid: Array<IntArray>, random: Random = Random.Default) {
        currentGrid = targetGrid
        coroutineScope {
            val jobs = reels.mapIndexed { col, reel ->
                val target = IntArray(SlotBoard.ROWS) { targetGrid[col][it] }
                val duration = 500L + col * 220L
                async { reel.rollAndLand(target, random, duration) }
            }
            jobs.awaitAll()
        }
    }
}

@Composable
fun rememberSlotGridState(initial: Array<IntArray>): SlotGridState =
    remember { SlotGridState(initial) }

/**
 * Per-column state. A long "reel" of symbol indexes is rendered as a vertical strip. The
 * offset animates from 0 (top visible) to (reel.size - ROWS) (target visible at the bottom).
 */
internal class SlotReelState(
    initialColumn: IntArray,
    private val pauseFlow: StateFlow<Boolean>,
) {
    val reel = mutableStateListOf<Int>()
    val offset = Animatable(0f)
    var settledSymbols: IntArray = initialColumn
        private set

    private var pendingAlign: Float? = null

    init {
        // Fill reel with random padding, then the initial visible column at the bottom.
        repeat(24) { reel.add((0 until SlotBoard.SYMBOLS.size).random()) }
        initialColumn.forEach { reel.add(it) }
        pendingAlign = (reel.size - SlotBoard.ROWS).toFloat()
    }

    /** Align the initial viewport on first composition. */
    suspend fun alignIfNeeded() {
        pendingAlign?.let {
            offset.snapTo(it)
            pendingAlign = null
        }
    }

    /** Rebuild the reel so it ends with [target] and animate to reveal it. */
    suspend fun rollAndLand(
        target: IntArray,
        random: Random,
        totalDurationMs: Long,
    ) {
        reel.clear()
        repeat(32) { reel.add(random.nextInt(SlotBoard.SYMBOLS.size)) }
        target.forEach { reel.add(it) }

        offset.snapTo(0f)
        val landingOffset = (reel.size - SlotBoard.ROWS).toFloat()
        animateLandingPausable(landingOffset, totalDurationMs.toInt())

        // Small bounce at landing (hold off if paused on the boundary).
        pauseFlow.first { !it }
        offset.animateTo(
            targetValue = landingOffset - 0.18f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        pauseFlow.first { !it }
        offset.animateTo(
            targetValue = landingOffset,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        settledSymbols = target
    }

    /**
     * Animate [offset] toward [target] while honoring [pauseFlow]. When pause flips to
     * true mid-animation, the reel stops at its current position; on resume, it picks up
     * from that offset with a duration proportional to the remaining distance.
     */
    private suspend fun animateLandingPausable(target: Float, totalMs: Int) {
        val startValue = offset.value
        val totalDist = target - startValue
        if (totalDist == 0f) return

        coroutineScope {
            val stopJob = launch {
                pauseFlow.collect { paused ->
                    if (paused) offset.stop()
                }
            }
            try {
                while (offset.value != target) {
                    pauseFlow.first { !it }
                    val remainDist = target - offset.value
                    val remainMs = (totalMs * (remainDist / totalDist))
                        .toInt()
                        .coerceAtLeast(1)
                    try {
                        offset.animateTo(
                            targetValue = target,
                            animationSpec = tween(remainMs, easing = EaseOutCubic)
                        )
                    } catch (e: CancellationException) {
                        // Pause-triggered stop() cancels the mutator; bail only if the
                        // outer scope itself was cancelled.
                        currentCoroutineContext().ensureActive()
                    }
                }
            } finally {
                stopJob.cancel()
            }
        }
    }
}

@Composable
fun SlotGrid(
    state: SlotGridState,
    matchedCells: Set<MatchedCell>,
    modifier: Modifier = Modifier,
    cellSpacing: Dp = 6.dp,
    maxCellSize: Dp = 96.dp,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(EgyptOverlay)
            .border(
                width = 2.dp,
                brush = Brush.verticalGradient(listOf(EgyptGold, EgyptGoldDeep)),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(cellSpacing),
    ) {
        // Row spacing between columns: (COLS - 1) gaps.
        val totalSpacing = cellSpacing * (SlotBoard.COLS - 1)
        val computedCellSize = ((maxWidth - totalSpacing) / SlotBoard.COLS)
            .coerceAtMost(maxCellSize)

        Row(
            horizontalArrangement = Arrangement.spacedBy(cellSpacing, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
        ) {
            for (col in 0 until SlotBoard.COLS) {
                SlotColumn(
                    reelState = state.reels[col],
                    cellSize = computedCellSize,
                    cellSpacing = cellSpacing,
                    col = col,
                    matchedCells = matchedCells,
                )
            }
        }
    }
}

@Composable
private fun SlotColumn(
    reelState: SlotReelState,
    cellSize: Dp,
    cellSpacing: Dp,
    col: Int,
    matchedCells: Set<MatchedCell>,
) {
    val density = LocalDensity.current
    val cellPxPlusSpacing = with(density) { (cellSize + cellSpacing).toPx() }

    LaunchedEffect(reelState) { reelState.alignIfNeeded() }

    val offsetValue = reelState.offset.value
    val viewportHeight = cellSize * SlotBoard.ROWS + cellSpacing * (SlotBoard.ROWS - 1)

    Box(
        modifier = Modifier
            .width(cellSize)
            .height(viewportHeight)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x33000000))
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(cellSpacing),
            modifier = Modifier
                .wrapContentHeight(align = Alignment.Top, unbounded = true)
                .offset {
                    IntOffset(
                        x = 0,
                        y = -(offsetValue * cellPxPlusSpacing).toInt()
                    )
                }
        ) {
            reelState.reel.forEachIndexed { index, symbolIdx ->
                val relativeRow = index - (reelState.reel.size - SlotBoard.ROWS)
                val isMatched = relativeRow in 0 until SlotBoard.ROWS &&
                        matchedCells.contains(MatchedCell(col, relativeRow))
                SlotCell(
                    symbolIdx = symbolIdx,
                    size = cellSize,
                    matched = isMatched,
                )
            }
        }
    }
}

@Composable
private fun SlotCell(
    symbolIdx: Int,
    size: Dp,
    matched: Boolean,
) {
    val transition = rememberInfiniteTransition(label = "match-pulse")
    val pulse by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(700), repeatMode = RepeatMode.Reverse
        ),
        label = "match-pulse-scale"
    )
    val haloScale by transition.animateFloat(
        initialValue = 1.05f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(900), repeatMode = RepeatMode.Reverse
        ),
        label = "match-halo-scale"
    )
    val glowAlpha by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700), repeatMode = RepeatMode.Reverse
        ),
        label = "match-glow-alpha"
    )
    val borderAlpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500), repeatMode = RepeatMode.Reverse
        ),
        label = "match-border-alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (matched) pulse else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "cell-scale"
    )
    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center,
    ) {
        if (matched) {
            // Soft outer halo that breathes beyond the cell bounds.
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = haloScale
                        scaleY = haloScale
                        alpha = glowAlpha
                    }
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                EgyptSand.copy(alpha = 0.85f),
                                EgyptGold.copy(alpha = 0.55f),
                                Color.Transparent,
                            )
                        ),
                        shape = RoundedCornerShape(14.dp),
                    )
            )
            // Bright inner glow behind the symbol.
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                EgyptSand.copy(alpha = 0.9f),
                                EgyptGold.copy(alpha = 0.4f),
                                Color.Transparent,
                            )
                        )
                    )
            )
            // Shimmering gold border ring.
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .graphicsLayer { alpha = borderAlpha }
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                EgyptSand,
                                EgyptGold,
                                EgyptGoldDeep,
                                EgyptGold,
                                EgyptSand,
                            )
                        ),
                        shape = RoundedCornerShape(10.dp),
                    )
            )
        }
        Image(
            painter = painterResource(SlotBoard.SYMBOLS[symbolIdx]),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(size)
                .padding(4.dp)
        )
    }
}
