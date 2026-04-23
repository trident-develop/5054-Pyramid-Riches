package com.dragonest.artifacts.goo.game

import com.dragonest.artifacts.goo.R
import kotlin.random.Random

/**
 * Board layout is 4 columns x 5 rows.
 * The grid is addressed as grid[col][row].
 */
object SlotBoard {
    const val COLS = 4
    const val ROWS = 5

    val SYMBOLS: IntArray = intArrayOf(
        R.drawable.egypt_el_1,
        R.drawable.egypt_el_2,
        R.drawable.egypt_el_3,
        R.drawable.egypt_el_4,
        R.drawable.egypt_el_5,
        R.drawable.egypt_el_6,
        R.drawable.egypt_el_7,
    )

    /**
     * Chance per spin to plant a guaranteed 3-in-a-row line on top of the uniform
     * roll. Lifts the perceived match rate without touching the scoring curve.
     */
    private const val MATCH_BOOST_CHANCE = 0.30f

    /** Generate a random 4x5 grid of symbol indexes (0 .. SYMBOLS.size - 1). */
    fun randomGrid(random: Random = Random.Default): Array<IntArray> {
        val grid = Array(COLS) { IntArray(ROWS) { random.nextInt(SYMBOLS.size) } }
        if (random.nextFloat() < MATCH_BOOST_CHANCE) plantLine(grid, random)
        return grid
    }

    private fun plantLine(grid: Array<IntArray>, random: Random) {
        val sym = random.nextInt(SYMBOLS.size)
        if (random.nextBoolean()) {
            val row = random.nextInt(ROWS)
            val startCol = random.nextInt(COLS - 2)
            repeat(3) { grid[startCol + it][row] = sym }
        } else {
            val col = random.nextInt(COLS)
            val startRow = random.nextInt(ROWS - 2)
            repeat(3) { grid[col][startRow + it] = sym }
        }
    }
}

/** A cell that took part in a match line. */
data class MatchedCell(val col: Int, val row: Int)

/** Result of analysing a settled grid. */
data class SpinResult(
    val score: Int,
    val matchedCells: Set<MatchedCell>,
    val lineCount: Int,
)

object SpinAnalyzer {

    /**
     * Score horizontal and vertical runs of 3+ identical symbols.
     * Scoring curve favours longer lines and awards a modest multi-line bonus.
     */
    fun analyze(grid: Array<IntArray>): SpinResult {
        val cells = HashSet<MatchedCell>()
        var score = 0
        var lines = 0

        // Horizontal runs (scan each row across columns).
        for (row in 0 until SlotBoard.ROWS) {
            var col = 0
            while (col < SlotBoard.COLS) {
                val sym = grid[col][row]
                var end = col
                while (end + 1 < SlotBoard.COLS && grid[end + 1][row] == sym) end++
                val len = end - col + 1
                if (len >= 3) {
                    for (c in col..end) cells.add(MatchedCell(c, row))
                    score += scoreForLen(len)
                    lines++
                }
                col = end + 1
            }
        }

        // Vertical runs (scan each column down rows).
        for (col in 0 until SlotBoard.COLS) {
            var row = 0
            while (row < SlotBoard.ROWS) {
                val sym = grid[col][row]
                var end = row
                while (end + 1 < SlotBoard.ROWS && grid[col][end + 1] == sym) end++
                val len = end - row + 1
                if (len >= 3) {
                    for (r in row..end) cells.add(MatchedCell(col, r))
                    score += scoreForLen(len)
                    lines++
                }
                row = end + 1
            }
        }

        if (lines >= 2) score += 15 * (lines - 1)

        return SpinResult(score = score, matchedCells = cells, lineCount = lines)
    }

    private fun scoreForLen(len: Int): Int = when (len) {
        3 -> 15
        4 -> 40
        else -> 80
    }
}
