package com.github.joaophi.yeelight

sealed class Effect(val value: String, open val duration: Int) {
    object Sudden : Effect(value = "sudden", duration = 0)
    data class Smooth(override val duration: Int) : Effect(value = "smooth", duration)
}