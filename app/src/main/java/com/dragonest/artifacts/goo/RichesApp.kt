package com.dragonest.artifacts.goo

import android.app.Application
import com.dragonest.artifacts.goo.di.dataStoreModule
import com.dragonest.artifacts.goo.di.gameModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class RichesApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@RichesApp)
            modules(
                dataStoreModule,
                gameModule
            )
        }
    }
}