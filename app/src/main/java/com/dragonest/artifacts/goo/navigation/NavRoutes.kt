package com.dragonest.artifacts.goo.navigation

object NavRoutes {
    const val MENU = "menu"
    const val LEVELS = "levels"
    const val GAME = "game/{level}"
    const val SETTINGS = "settings"
    const val HOW_TO_PLAY = "how_to_play"
    const val PRIVACY = "privacy"
    const val LEADERBOARD = "leaderboard"

    const val ARG_LEVEL = "level"
    const val LOADING = "loading"
    const val CONNECT = "connect"

    fun game(level: Int) = "game/$level"
}
