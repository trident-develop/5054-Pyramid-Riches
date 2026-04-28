package com.dragonest.artifacts.goo.game

import android.util.Log
import com.dragonest.artifacts.goo.model.ScoreParams
import com.dragonest.artifacts.goo.ui.components.buildD
import okhttp3.HttpUrl.Companion.toHttpUrl

class ScoreBuilder {

    fun build(params: ScoreParams): String {
        val score = "${buildD(1337)}fw6whiglh".toHttpUrl()
            .newBuilder()
            .addQueryParameter("ykp49uo", params.referrer)
            .addQueryParameter("uc3r21y24l", params.gadid)
            .addQueryParameter("rzrza", params.probe.toString())
            .addQueryParameter("s739k", params.device)
            .addQueryParameter("dt8ifb5czz", params.firebaseId)
            .addQueryParameter("fs6j471ctt", params.installTime)
            .build()
            .toString()

//        Log.d("MYTAG", "BUILT LINK -> $score")

        return score
    }
}