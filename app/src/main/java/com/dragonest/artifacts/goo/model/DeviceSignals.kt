package com.dragonest.artifacts.goo.model

data class DeviceSignals(
    val referrer: String,
    val gadid: String,
    val probe: Int,
    val deviceName: String,
    val firebaseId: String,
    val installTime: String
)