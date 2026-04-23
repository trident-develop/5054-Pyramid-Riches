package com.dragonest.artifacts.goo.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // --- Settings ---
    var musicEnabled: Boolean
        get() = prefs.getBoolean(KEY_MUSIC, true)
        set(value) = prefs.edit().putBoolean(KEY_MUSIC, value).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND, value).apply()

    // --- Level progression ---
    /** Highest unlocked level (1-based). Minimum is 1. */
    var unlockedLevel: Int
        get() = prefs.getInt(KEY_UNLOCKED_LEVEL, 1).coerceAtLeast(1)
        set(value) = prefs.edit().putInt(KEY_UNLOCKED_LEVEL, value.coerceIn(1, TOTAL_LEVELS)).apply()

    /** Best score achieved on a specific level (0 if never played). */
    fun bestScoreForLevel(level: Int): Int = prefs.getInt(keyBestLevel(level), 0)

    /** Sum of the player's best scores across every level — their leaderboard total. */
    fun totalPlayerScore(): Int {
        var sum = 0
        for (level in 1..TOTAL_LEVELS) sum += bestScoreForLevel(level)
        return sum
    }

    fun setBestScoreForLevel(level: Int, score: Int) {
        if (score > bestScoreForLevel(level)) {
            prefs.edit().putInt(keyBestLevel(level), score).apply()
        }
    }

    // --- Leaderboard / stats ---
    var bestScore: Int
        get() = prefs.getInt(KEY_BEST_SCORE, 0)
        set(value) = prefs.edit().putInt(KEY_BEST_SCORE, value).apply()

    var totalWins: Int
        get() = prefs.getInt(KEY_WINS, 0)
        set(value) = prefs.edit().putInt(KEY_WINS, value).apply()

    var totalLosses: Int
        get() = prefs.getInt(KEY_LOSSES, 0)
        set(value) = prefs.edit().putInt(KEY_LOSSES, value).apply()

    fun registerWin(level: Int, score: Int) {
        totalWins += 1
        if (score > bestScore) bestScore = score
        setBestScoreForLevel(level, score)
        if (level == unlockedLevel && level < TOTAL_LEVELS) {
            unlockedLevel = level + 1
        }
    }

    fun registerLoss(level: Int, score: Int) {
        totalLosses += 1
        if (score > bestScore) bestScore = score
        setBestScoreForLevel(level, score)
    }

    companion object {
        const val TOTAL_LEVELS = 33

        private const val PREFS_NAME = "pyramid_riches_prefs"
        private const val KEY_MUSIC = "music_enabled"
        private const val KEY_SOUND = "sound_enabled"
        private const val KEY_UNLOCKED_LEVEL = "unlocked_level"
        private const val KEY_BEST_SCORE = "best_score"
        private const val KEY_WINS = "total_wins"
        private const val KEY_LOSSES = "total_losses"

        private fun keyBestLevel(level: Int) = "best_level_$level"

        @Volatile
        private var instance: PreferencesManager? = null

        fun get(context: Context): PreferencesManager =
            instance ?: synchronized(this) {
                instance ?: PreferencesManager(context).also { instance = it }
            }
    }
}
