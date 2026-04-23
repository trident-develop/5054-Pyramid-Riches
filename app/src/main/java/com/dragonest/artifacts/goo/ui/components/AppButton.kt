package com.dragonest.artifacts.goo.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.dragonest.artifacts.goo.R
import com.dragonest.artifacts.goo.ui.modifier.pressableWithCooldown
import com.dragonest.artifacts.goo.ui.theme.EgyptFontFamily
import com.dragonest.artifacts.goo.ui.theme.EgyptShadow

/**
 * Primary menu button with text on top of [main_button] drawable.
 */
@Composable
fun MainButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 240.dp,
    height: Dp = 70.dp,
    enabled: Boolean = true,
    textColor: Color = Color(0xFFFFF4C8),
    fontSize: Int = 22,
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .pressableWithCooldown(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.main_button),
            contentDescription = text,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.wrapContentSize().width(width).height(height)
        )
        Text(
            text = text.uppercase(),
            style = TextStyle(
                fontFamily = EgyptFontFamily,
                fontSize = fontSize.sp,
                color = textColor,
                textAlign = TextAlign.Center,
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = EgyptShadow,
                    offset = androidx.compose.ui.geometry.Offset(1f, 3f),
                    blurRadius = 4f,
                ),
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Icon-only button rendered as a tappable drawable.
 */
@Composable
fun IconImageButton(
    @DrawableRes drawableRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    enabled: Boolean = true,
    cooldownMillis: Long = 1000L,
    contentDescription: String? = null,
) {
    Image(
        painter = painterResource(drawableRes),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(size)
            .pressableWithCooldown(enabled = enabled, onClick = onClick, cooldownMillis = cooldownMillis),
    )
}
