package com.dragonest.artifacts.goo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dragonest.artifacts.goo.R

/**
 * Full-screen dim + centered popup_1 card. Children stack inside the card.
 */
@Composable
fun EgyptPopup(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { /* consume taps */ } }
            .background(Color.Black.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(0.6f)
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.popup_1),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                content()
            }
        }
    }
}

@Composable
fun PausePopup(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit,
) {
    EgyptPopup {
        ScreenTitle(text = "Paused", fontSize = 28)
        Spacer(Modifier.height(16.dp))
        MainButton(text = "Resume", onClick = onResume, width = 220.dp, height = 62.dp)
        Spacer(Modifier.height(10.dp))
        MainButton(text = "Restart", onClick = onRestart, width = 220.dp, height = 62.dp)
        Spacer(Modifier.height(10.dp))
        MainButton(text = "Menu", onClick = onMenu, width = 220.dp, height = 62.dp)
    }
}

@Composable
fun ResultPopup(
    won: Boolean,
    score: Int,
    canAdvance: Boolean,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onMenu: () -> Unit,
) {
    EgyptPopup {
        Image(
            painter = painterResource(if (won) R.drawable.win else R.drawable.lose),
            contentDescription = if (won) "You won" else "You lost",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(width = 220.dp, height = 110.dp)
        )
        Spacer(Modifier.height(10.dp))
        ScreenTitle(text = "Score: $score", fontSize = 22)
        Spacer(Modifier.height(14.dp))
        if (won && canAdvance) {
            MainButton(text = "Next Level", onClick = onNextLevel, width = 220.dp, height = 62.dp)
            Spacer(Modifier.height(10.dp))
        }
        MainButton(text = "Replay", onClick = onReplay, width = 220.dp, height = 62.dp)
        Spacer(Modifier.height(10.dp))
        MainButton(text = "Menu", onClick = onMenu, width = 220.dp, height = 62.dp)
    }
}
