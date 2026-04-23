package com.dragonest.artifacts.goo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
import com.dragonest.artifacts.goo.ui.modifier.pressableWithCooldown
import com.dragonest.artifacts.goo.ui.theme.EgyptFontFamily
import com.dragonest.artifacts.goo.ui.theme.EgyptGold

@Composable
fun LevelsScreen(
    onBack: () -> Unit,
    onLevelChosen: (Int) -> Unit,
) {
    val context = LocalContext.current
    val prefs = PreferencesManager.get(context)
    val unlocked = prefs.unlockedLevel

    EgyptBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .padding(top = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconImageButton(
                    drawableRes = R.drawable.back_button,
                    onClick = onBack,
                    size = 52.dp,
                    contentDescription = "Back",
                )
                ScreenTitle(text = "Levels", fontSize = 32)
                Spacer(Modifier.size(52.dp))
            }

            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(10.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(PreferencesManager.TOTAL_LEVELS) { idx ->
                    val level = idx + 1
                    val isUnlocked = level <= unlocked
                    LevelTile(
                        level = level,
                        unlocked = isUnlocked,
                        onClick = { if (isUnlocked) onLevelChosen(level) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelTile(
    level: Int,
    unlocked: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .pressableWithCooldown(enabled = unlocked, onClick = onClick)
            .graphicsLayer {
                alpha = if (unlocked) 1f else 0.85f
            },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(
                if (unlocked) R.drawable.level_open_button else R.drawable.level_close_button
            ),
            contentDescription = "Level $level",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
        )

        Text(
            text = level.toString(),
            style = TextStyle(
                fontFamily = EgyptFontFamily,
                fontSize = 26.sp,
                color = Color(0xFFFFF1BC),
                textAlign = TextAlign.Center,
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color(0xAA1A0C02),
                    offset = androidx.compose.ui.geometry.Offset(1f, 2f),
                    blurRadius = 4f,
                )
            )
        )
    }
}