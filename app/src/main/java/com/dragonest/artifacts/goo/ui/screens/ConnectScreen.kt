package com.dragonest.artifacts.goo.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dragonest.artifacts.goo.R
import com.dragonest.artifacts.goo.navigation.NavRoutes
import com.dragonest.artifacts.goo.ui.components.MainButton
import com.dragonest.artifacts.goo.ui.theme.EgyptFontFamily

@Composable
fun ConnectScreen(navController: NavController) {

    var showButton by remember { mutableStateOf(true) }
    var showConnecting by remember { mutableStateOf(false) }
    val context = LocalContext.current

    BackHandler(enabled = true) {}

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .aspectRatio(0.6f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Check your\ninternet connection",
                        fontSize = 30.sp,
                        fontFamily = EgyptFontFamily,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Image(
                        painter = painterResource(R.drawable.wifi),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "LOST CONNECTION",
                        fontSize = 16.sp,
                        fontFamily = EgyptFontFamily,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    when {
                        showConnecting -> {
                            AnimatedVisibility(
                                visible = showConnecting,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Text(
                                    text = "Connecting...",
                                    fontSize = 22.sp,
                                    fontFamily = EgyptFontFamily,
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

                        showButton -> {
                            MainButton(
                                text = "Try Again",
                                enabled = showButton,
                                onClick = {
                                    showButton = false
                                    showConnecting = true

                                    if (context.isEgyptConnected()) {
                                        navController.navigate(NavRoutes.LOADING) {
                                            popUpTo(NavRoutes.CONNECT) { inclusive = true }
                                        }
                                    } else {
                                        showButton = true
                                        showConnecting = false
                                    }
                                }
                            )
                        }

                        else -> {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("ServiceCast")
fun Context.isEgyptConnected(): Boolean {
    val ballConnectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeBallNetwork = ballConnectivityManager.activeNetwork
    val ballCapabilities = ballConnectivityManager.getNetworkCapabilities(activeBallNetwork)

    return ballCapabilities?.run {
        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    } == true
}