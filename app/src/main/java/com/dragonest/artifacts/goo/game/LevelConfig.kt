package com.dragonest.artifacts.goo.game

/**
 * Per-level tuning. Kept as a pure function so the 33-level curve is explicit in one place.
 *
 * - targetScore grows roughly linearly so later levels demand better spins.
 * - attempts grow with the level to offset the higher targets.
 */
data class LevelConfig(
    val level: Int,
    val targetScore: Int,
    val attempts: Int,
)

object Levels {
    const val TOTAL = 33

    fun configFor(level: Int): LevelConfig {
        val clamped = level.coerceIn(1, TOTAL)
        val targetScore = 60 + (clamped - 1) * 45        // 60 .. 60 + 32*45 = 1500
        val attempts = 10 + (clamped - 1) * 2            // 10, 12, 14, ..., 74
        return LevelConfig(clamped, targetScore, attempts)
    }
}
