package com.dragonest.artifacts.goo.event

sealed interface TVEvent {
    data object OpenGame : TVEvent
}