package com.dragonest.artifacts.goo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dragonest.artifacts.goo.data.GameRepo
import com.dragonest.artifacts.goo.navigation.LoadingGraph
import com.dragonest.artifacts.goo.ui.screens.TV3
import org.koin.android.ext.android.inject
import kotlin.getValue

class LoadingActivity : ComponentActivity() {
    lateinit var TV3: TV3
    private val gameRepo: GameRepo by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemBars()
        TV3 = TV3(this, gameRepo)
        setContent {
            LoadingGraph(TV3)
        }
    }

    private fun hideSystemBars() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    override fun onDestroy() {
        TV3.destroy()
        super.onDestroy()
    }
}
