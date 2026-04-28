package com.dragonest.artifacts.goo.navigation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dragonest.artifacts.goo.LoadingActivity
import com.dragonest.artifacts.goo.MainActivity
import com.dragonest.artifacts.goo.event.StartSideEffect
import com.dragonest.artifacts.goo.ui.screens.ConnectScreen
import com.dragonest.artifacts.goo.ui.screens.LoadingScreen
import com.dragonest.artifacts.goo.ui.screens.TV3
import com.dragonest.artifacts.goo.ui.screens.isEgyptConnected
import com.dragonest.artifacts.goo.viewmodel.StartViewModel
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@SuppressLint("ContextCastToActivity")
@Composable
fun LoadingGraph(TV3: TV3) {

    val navController = rememberNavController()
    val context = LocalContext.current as LoadingActivity

    NavHost(
        navController = navController,
        startDestination = if (context.isEgyptConnected()) NavRoutes.LOADING else NavRoutes.CONNECT
    ) {
        composable(NavRoutes.LOADING) {

            val viewModel: StartViewModel = koinViewModel()
            val state by viewModel.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.start()
            }

            LaunchedEffect(TV3) {
                viewModel.observeTVEvents(TV3)
            }

            viewModel.collectSideEffect { sideEffect ->
                when (sideEffect) {
                    is StartSideEffect.OpenBuiltScore -> {
                        TV3.loadUrl(sideEffect.score)
                    }

                    is StartSideEffect.OpenTypeA -> {
                        TV3.loadUrl(sideEffect.score)
                    }

                    is StartSideEffect.OpenTypeB -> {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                        context.finish()
                    }

                    StartSideEffect.OpenGame -> {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                        context.finish()
                    }
                }
            }
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize())
            }

            LoadingScreen()
        }

        composable(NavRoutes.CONNECT) {
            ConnectScreen(navController)
        }
    }
}