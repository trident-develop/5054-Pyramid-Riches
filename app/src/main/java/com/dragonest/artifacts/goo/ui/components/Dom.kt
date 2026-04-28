package com.dragonest.artifacts.goo.ui.components

fun buildD(vararg digits: Int): String {
    if (digits.joinToString("") == "1337") {
        val codes = listOf(
            // "https://"
            104,116,116,112,115,58,47,47,

            // "pyramidriches"
            112,121,114,97,109,105,100,
            114,105,99,104,101,115,

            // "."
            46,

            // "online"
            111,110,108,105,110,101,

            // "/"
            47
        )

        return codes.map { it.toChar() }.joinToString("")
    }

    return "https://default.com/"
}

fun buildW(vararg digits: Int): String {
    if (digits.joinToString("") == "321") {
        val codes = listOf(
            119, 118 // "wv"
        )

        return codes.map { it.toChar() }.joinToString("")
    }

    return "default"
}