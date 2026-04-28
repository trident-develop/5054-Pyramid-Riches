package com.dragonest.artifacts.goo.game

import android.util.Log
import com.dragonest.artifacts.goo.data.GameRepo
import com.dragonest.artifacts.goo.event.StartDestination

class ResolveStartFlowUseCase(
    private val gameRepo: GameRepo,
    private val paramsCollector: ScoreParamsCollector,
    private val linkBuilder: ScoreBuilder,
    private val savedScoreRouter: SavedScoreRouter
) {

    suspend operator fun invoke(): StartDestination {
        val savedScore = gameRepo.getSavedScore()

        return if (savedScore.isNullOrBlank()) {
//            Log.d("MYTAG", "SAVED LINK IS EMPTY -> BUILD NEW LINK")

            val params = paramsCollector.collect()
            val builtLink = linkBuilder.build(params)

            StartDestination.BuiltScore(builtLink)

        } else {
//            Log.d("MYTAG", "SAVED LINK EXISTS -> $savedScore")

            savedScoreRouter.score(savedScore)
        }
    }
}