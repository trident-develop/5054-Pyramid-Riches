package com.dragonest.artifacts.goo.ui.screens

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.rememberNavController
import com.dragonest.artifacts.goo.R
import com.dragonest.artifacts.goo.ui.components.EgyptBackground
import com.dragonest.artifacts.goo.ui.components.EgyptBodyText
import com.dragonest.artifacts.goo.ui.components.IconImageButton
import com.dragonest.artifacts.goo.ui.components.ScreenTitle

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {

    var loadWeb by remember { mutableStateOf(true) }

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
                ScreenTitle(text = "Privacy Policy", fontSize = 26)
                Spacer(Modifier.size(52.dp))
            }

            Spacer(Modifier.height(16.dp))

            AndroidView(
                factory = { context ->
                    FrameLayout(context).apply {
                        val webView = WebView(context).apply {
                            setInitialScale(100)
                            settings.setSupportZoom(true)
                            settings.builtInZoomControls = true
                            settings.displayZoomControls = false
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    loadWeb = false
                                }

                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    return false
                                }
                            }
                            loadUrl("https://telegra.ph/Privacy-Policy-for-Pyramid-Riches-04-23")
                        }
                        addView(
                            webView, FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
            )
        }

        if (loadWeb) {
            LinearProgressIndicator(
                color = Color.Yellow,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .align(Alignment.Center)
            )
        }
    }
}