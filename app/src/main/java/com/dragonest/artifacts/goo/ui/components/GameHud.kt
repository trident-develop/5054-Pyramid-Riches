package com.dragonest.artifacts.goo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dragonest.artifacts.goo.R
import com.dragonest.artifacts.goo.ui.theme.EgyptFontFamily
import com.dragonest.artifacts.goo.ui.theme.EgyptGold

/**
 * Top row of the game screen: back + pause buttons flanking a level title,
 * with a score/target/attempts strip below.
 */
@Composable
fun GameHud(
    level: Int,
    score: Int,
    targetScore: Int,
    attempts: Int,
    onBack: () -> Unit,
    onPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconImageButton(
                drawableRes = R.drawable.back_button,
                onClick = onBack,
                size = 52.dp,
                contentDescription = "Back",
            )
            ScreenTitle(text = "Level $level", fontSize = 26)
            IconImageButton(
                drawableRes = R.drawable.pause_button,
                onClick = onPause,
                size = 52.dp,
                contentDescription = "Pause",
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ScoreChip(label = "SCORE", value = score.toString(), modifier = Modifier.weight(1f))
            ScoreChip(label = "TARGET", value = targetScore.toString(), modifier = Modifier.weight(1f))
            ScoreChip(label = "TRIES", value = attempts.toString(), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ScoreChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 90.dp)
            .height(60.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.score_bg),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxWidth().height(60.dp)
        )
        Column(
            modifier = Modifier.padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = EgyptFontFamily,
                    fontSize = 11.sp,
                    color = EgyptGold,
                    textAlign = TextAlign.Center,
                )
            )
            Text(
                text = value,
                style = TextStyle(
                    fontFamily = EgyptFontFamily,
                    fontSize = 20.sp,
                    color = Color(0xFF8D5B12),
                    textAlign = TextAlign.Center,
                )
            )
        }
    }
}
