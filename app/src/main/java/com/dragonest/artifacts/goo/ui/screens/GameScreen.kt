package com.dragonest.artifacts.goo.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.rememberNavController
import com.dragonest.artifacts.goo.audio.LocalAudioManager
import com.dragonest.artifacts.goo.data.PreferencesManager
import com.dragonest.artifacts.goo.game.Levels
import com.dragonest.artifacts.goo.game.MatchedCell
import com.dragonest.artifacts.goo.game.SlotBoard
import com.dragonest.artifacts.goo.game.SpinAnalyzer
import com.dragonest.artifacts.goo.ui.components.EgyptBackground
import com.dragonest.artifacts.goo.ui.components.GameHud
import com.dragonest.artifacts.goo.ui.components.MainButton
import com.dragonest.artifacts.goo.ui.components.PausePopup
import com.dragonest.artifacts.goo.ui.components.ResultPopup
import com.dragonest.artifacts.goo.ui.components.SlotGrid
import com.dragonest.artifacts.goo.ui.components.rememberSlotGridState
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun GameScreen(
    level: Int,
    onBackToMenu: () -> Unit,
    onBackToLevels: () -> Unit,
    onGoToLevel: (Int) -> Unit,
) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager.get(context) }
    val audio = LocalAudioManager.current
    val scope = rememberCoroutineScope()

    val config = remember(level) { Levels.configFor(level) }

    var score by remember(level) { mutableIntStateOf(0) }
    var attemptsLeft by remember(level) { mutableIntStateOf(config.attempts) }
    var matchedCells by remember { mutableStateOf(emptySet<MatchedCell>()) }
    var spinning by remember { mutableStateOf(false) }
    var paused by remember { mutableStateOf(false) }
    var resultWon by remember { mutableStateOf<Boolean?>(null) }
    var resultRegistered by remember(level) { mutableStateOf(false) }

    val initialGrid = remember(level) { SlotBoard.randomGrid() }
    val gridState = rememberSlotGridState(initialGrid)

    var isExitingScreen by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE && resultWon != true && resultWon != false && !isExitingScreen)
                paused = true
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose { lifecycleOwner.lifecycle.removeObserver(obs) }
    }

    BackHandler(enabled = true) {
        isExitingScreen = true
        onBackToLevels()
    }

    LaunchedEffect(paused, gridState) {
        gridState.setPaused(paused)
    }

    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(durationMillis = 450),
        label = "score"
    )

    fun finishIfNeeded() {
        if (resultWon != null) return
        when {
            score >= config.targetScore -> {
                resultWon = true
                if (!resultRegistered) {
                    prefs.registerWin(level, score)
                    resultRegistered = true
                }
                audio.playWin()
            }
            attemptsLeft <= 0 -> {
                resultWon = false
                if (!resultRegistered) {
                    prefs.registerLoss(level, score)
                    resultRegistered = true
                }
                audio.playLose()
            }
        }
    }

    fun resetLevel() {
        score = 0
        attemptsLeft = config.attempts
        matchedCells = emptySet()
        spinning = false
        paused = false
        resultWon = null
        resultRegistered = false
    }

    val onSpin: () -> Unit = {
        if (!spinning && resultWon == null && attemptsLeft > 0) {
            spinning = true
            matchedCells = emptySet()
            audio.playSpin()
            scope.launch {
                val target = SlotBoard.randomGrid(Random.Default)
                gridState.spinTo(target, Random.Default)
                val analysis = SpinAnalyzer.analyze(target)
                matchedCells = analysis.matchedCells
                score += analysis.score
                attemptsLeft -= 1
                spinning = false
                finishIfNeeded()
            }
        }
    }

    EgyptBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            GameHud(
                level = level,
                score = animatedScore,
                targetScore = config.targetScore,
                attempts = attemptsLeft,
                onBack = {
                    isExitingScreen = true
                    onBackToLevels()
                },
                onPause = { if (resultWon == null) paused = true },
            )

            SlotGrid(
                state = gridState,
                matchedCells = matchedCells,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
            )

            MainButton(
                text = if (spinning) "Turning..." else "Turn",
                onClick = onSpin,
                enabled = !spinning && resultWon == null && attemptsLeft > 0 && !paused,
                modifier = Modifier.padding(horizontal = 30.dp),
                width = 240.dp,
                height = 76.dp,
                fontSize = 26,
            )
        }
    }

    if (paused) {
        PausePopup(
            onResume = { paused = false },
            onRestart = { resetLevel() },
            onMenu = {
                isExitingScreen = true
                onBackToMenu()
            },
        )
    }

    resultWon?.let { won ->
        ResultPopup(
            won = won,
            score = score,
            canAdvance = won && level < Levels.TOTAL,
            onNextLevel = {
                if (level < Levels.TOTAL) onGoToLevel(level + 1) else onBackToLevels()
            },
            onReplay = { resetLevel() },
            onMenu = {
                isExitingScreen = true
                onBackToMenu()
            },
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)

@Preview(
    showBackground = true,
    showSystemUi = true,
    widthDp = 360,
    heightDp = 640
)

@Preview(
    name = "mdpi (160)",
    widthDp = 320,
    heightDp = 680,
    fontScale = 1.0f,
    showBackground = true,
    showSystemUi = true
)

@Preview(
    name = "hdpi (240)",
    widthDp = 450,
    heightDp = 800,
    fontScale = 1.0f,
    showBackground = true,
    showSystemUi = true
)

@Composable
private fun ScreenPreview() {
    val navController = rememberNavController()
    GameScreen(
        level = 10,
        onBackToMenu = {},
        onBackToLevels = {},
        onGoToLevel = { _ -> }
    )
}