package com.dragonest.artifacts.goo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dragonest.artifacts.goo.R
import com.dragonest.artifacts.goo.ui.components.EgyptBackground
import com.dragonest.artifacts.goo.ui.components.EgyptBodyText
import com.dragonest.artifacts.goo.ui.components.IconImageButton
import com.dragonest.artifacts.goo.ui.components.ScreenTitle
import com.dragonest.artifacts.goo.ui.theme.EgyptGold
import com.dragonest.artifacts.goo.ui.theme.EgyptGoldDeep
import com.dragonest.artifacts.goo.ui.theme.EgyptSand

private val PanelTextColor = Color(0xFF442A02)

@Composable
fun HowToPlayScreen(onBack: () -> Unit) {
    EgyptBackground {
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
                ScreenTitle(text = "How To Play", fontSize = 28)
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 22.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    EgyptBodyText(
                        color = PanelTextColor,
                        text = "Welcome, adventurer, to Egypt Slots — a slot machine " +
                                "set deep beneath the pyramids."
                    )
                    EgyptBodyText(
                        color = PanelTextColor,
                        text = "The board has 4 columns and 5 rows. Each spin reshuffles " +
                                "ancient Egyptian symbols into a new pattern."
                    )
                    EgyptBodyText(
                        color = PanelTextColor,
                        text = "Tap SPIN to roll. Three or more matching symbols in a row " +
                                "or column form a winning line and award score points."
                    )
                    EgyptBodyText(
                        color = PanelTextColor,
                        text = "Longer lines reward you with more points. Forming several " +
                                "lines in one spin grants a multi-line bonus."
                    )
                    EgyptBodyText(
                        color = PanelTextColor,
                        text = "Every level has a target score and a limited number of spins. " +
                                "Reach the target before your spins run out to win."
                    )
                    EgyptBodyText(
                        color = PanelTextColor,
                        text = "Winning a level unlocks the next one. There are 33 levels in " +
                                "total, and each new level demands a little more than the last."
                    )
                    EgyptBodyText(
                        color = PanelTextColor,
                        text = "May fortune favor you — the treasure of the pharaohs awaits."
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
