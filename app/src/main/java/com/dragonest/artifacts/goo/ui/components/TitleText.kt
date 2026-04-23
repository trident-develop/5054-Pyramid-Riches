package com.dragonest.artifacts.goo.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.dragonest.artifacts.goo.ui.theme.EgyptFontFamily
import com.dragonest.artifacts.goo.ui.theme.EgyptGold
import com.dragonest.artifacts.goo.ui.theme.EgyptShadow

@Composable
fun ScreenTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = EgyptGold,
    fontSize: Int = 34,
) {
    Text(
        text = text.uppercase(),
        style = TextStyle(
            fontFamily = EgyptFontFamily,
            fontSize = fontSize.sp,
            color = color,
            textAlign = TextAlign.Center,
            shadow = androidx.compose.ui.graphics.Shadow(
                color = EgyptShadow,
                offset = androidx.compose.ui.geometry.Offset(2f, 4f),
                blurRadius = 8f,
            )
        ),
        modifier = modifier
    )
}

@Composable
fun EgyptBodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = EgyptGold,
    fontSize: Int = 18,
    textAlign: TextAlign = TextAlign.Start,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = color,
            fontSize = fontSize.sp,
            textAlign = textAlign,
        ),
        modifier = modifier
    )
}
