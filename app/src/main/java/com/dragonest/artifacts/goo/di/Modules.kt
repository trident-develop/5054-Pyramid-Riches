package com.dragonest.artifacts.goo.di

import androidx.activity.ComponentActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.dragonest.artifacts.goo.data.GameRepo
import com.dragonest.artifacts.goo.data.GameRepoImpl
import com.dragonest.artifacts.goo.data.gameDataStore
import com.dragonest.artifacts.goo.game.DeviceSignalsProvider
import com.dragonest.artifacts.goo.game.ResolveStartFlowUseCase
import com.dragonest.artifacts.goo.game.SavedScoreRouter
import com.dragonest.artifacts.goo.game.ScoreBuilder
import com.dragonest.artifacts.goo.game.ScoreParamsCollector
import com.dragonest.artifacts.goo.ui.screens.TV3
import com.dragonest.artifacts.goo.viewmodel.StartViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val gameModule = module {

    single<GameRepo> {
        GameRepoImpl(
            dataStore = get()
        )
    }

    single {
        ScoreBuilder()
    }

    single {
        SavedScoreRouter()
    }

    single<GameRepo> {
        GameRepoImpl(get())
    }

    single {
        DeviceSignalsProvider(
            context = androidContext()
        )
    }

    single {
        ScoreParamsCollector(
            signalsProvider = get()
        )
    }

    factory {
        ResolveStartFlowUseCase(
            gameRepo = get(),
            paramsCollector = get(),
            linkBuilder = get(),
            savedScoreRouter = get()
        )
    }

    viewModel {
        StartViewModel(
            resolveStartFlowUseCase = get()
        )
    }
}

val dataStoreModule = module {
    single<DataStore<Preferences>> {
        androidContext().gameDataStore
    }
}