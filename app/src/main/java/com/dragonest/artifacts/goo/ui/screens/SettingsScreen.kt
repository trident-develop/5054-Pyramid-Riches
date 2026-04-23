package com.dragonest.artifacts.goo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dragonest.artifacts.goo.R
import com.dragonest.artifacts.goo.audio.LocalAudioManager
import com.dragonest.artifacts.goo.data.PreferencesManager
import com.dragonest.artifacts.goo.ui.components.EgyptBackground
import com.dragonest.artifacts.goo.ui.components.EgyptBodyText
import com.dragonest.artifacts.goo.ui.components.IconImageButton
import com.dragonest.artifacts.goo.ui.components.MainButton
import com.dragonest.artifacts.goo.ui.components.ScreenTitle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onHowToPlay: () -> Unit,
    onPrivacy: () -> Unit,
) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager.get(context) }
    val audio = LocalAudioManager.current
    val isInPreview      = LocalInspectionMode.current
    var musicOn by remember { mutableStateOf(prefs.musicEnabled) }
    var soundOn by remember { mutableStateOf(prefs.soundEnabled) }

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
                ScreenTitle(text = "Settings", fontSize = 32)
                Spacer(Modifier.size(52.dp))
            }

            Spacer(Modifier.height(40.dp))

            ToggleRow(
                label = "Music",
                isOn = musicOn,
                onDrawable = R.drawable.music_on_button,
                offDrawable = R.drawable.music_off_button,
                onToggle = {
                    musicOn = !musicOn
                    audio.setMusicEnabled(musicOn)
                },
            )
            Spacer(Modifier.height(24.dp))
            ToggleRow(
                label = "Sound",
                isOn = soundOn,
                onDrawable = R.drawable.sound_on_button,
                offDrawable = R.drawable.sound_off_button,
                onToggle = {
                    soundOn = !soundOn
                    audio.setSoundEnabled(soundOn)
                },
            )

            Spacer(Modifier.height(40.dp))

            MainButton(text = "How To Play", onClick = onHowToPlay)
            Spacer(Modifier.height(14.dp))
            MainButton(text = "Privacy Policy", onClick = onPrivacy)
        }

        if (!isInPreview) {
            AndroidView(
                factory = {
                    val adView = AdView(it)
                    adView.setAdSize(AdSize.BANNER)
                    adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"
                    adView.loadAd(AdRequest.Builder().build())
                    adView
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    isOn: Boolean,
    onDrawable: Int,
    offDrawable: Int,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        EgyptBodyText(text = label, fontSize = 24)
        IconImageButton(
            drawableRes = if (isOn) onDrawable else offDrawable,
            onClick = onToggle,
            cooldownMillis = 0L,
            size = 72.dp,
            contentDescription = "$label ${if (isOn) "on" else "off"}",
        )
    }
}
