package com.dragonest.artifacts.goo.ui.modifier

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.dragonest.artifacts.goo.ui.components.buildD
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.URLDecoder
import java.util.Locale

@Composable
fun Modifier.pressableWithCooldown(
    cooldownMillis: Long = 1000L,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    var lastClickTimestamp by remember { mutableLongStateOf(0L) }
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.88f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "PressAnimation"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(enabled) {
            detectTapGestures(
                onPress = {
                    if (!enabled) return@detectTapGestures
                    val now = System.currentTimeMillis()
                    if (now - lastClickTimestamp >= cooldownMillis) {
                        lastClickTimestamp = now
                        isPressed = true
                        try {
                            tryAwaitRelease()
                            onClick()
                        } finally {
                            isPressed = false
                        }
                    }
                }
            )
        }
}

fun decodeUtf8(encoded: String?): String =
    URLDecoder.decode(encoded, "UTF-8")

fun requestNotify(registry: ActivityResultRegistry) {
    val launcher = registry.register(
        "requestPermissionKey",
        ActivityResultContracts.RequestPermission()
    ) {  }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

fun regToken() {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val fcmToken: String =
                runCatching { FirebaseMessaging.getInstance().token.await() }
                    .getOrElse { "null" }
            val locale = Locale.getDefault().toLanguageTag()
            val url = "${buildD(1337)}y96vh9fqbh/"
            val client = OkHttpClient()

            val fullUrl = "$url?" +
                    "oei1j8bs=${Firebase.analytics.appInstanceId.await()}" +
                    "&s6amwenlh=${decodeUtf8(fcmToken)}"

            val request = Request.Builder().url(fullUrl)
                .addHeader("Accept-Language", locale)
                .get().build()


            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                override fun onResponse(call: Call, response: Response) {
                    response.close()
                }
            })
        } catch (exc: Exception) {}
    }
}

fun postback(intent: Intent) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val trackingId = intent.getStringExtra("trackingId")
//            Log.d("MYTAG", "trackingId = $trackingId")

            if (trackingId.isNullOrEmpty()) {
                return@launch
            }

            val fcmToken: String =
                runCatching { FirebaseMessaging.getInstance().token.await() }
                    .getOrElse { "null" }

            val url = "${buildD(1337)}x54d0xskv8/"
            val client = OkHttpClient()

            val fullUrl = "$url?" +
                    "qr1eh=$trackingId" +
                    "&a7t5r=${decodeUtf8(fcmToken)}"

            val request = Request.Builder()
                .url(fullUrl)
                .get()
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                override fun onResponse(call: Call, response: Response) {
                    response.close()
                }
            })

        } catch (exc: Exception) {
        }
    }
}