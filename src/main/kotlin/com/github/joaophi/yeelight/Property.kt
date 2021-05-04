package com.github.joaophi.yeelight

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

sealed class Property<T>(val name: String, val parse: (String) -> T) {
    object Power : Property<Boolean>(name = "power", parse = String::toBoolean)
    object Bright : Property<Int>(name = "bright", parse = String::toInt)
    object CT : Property<Int>(name = "ct", parse = String::toInt)
    object RGB : Property<Int>(name = "rgb", parse = String::toInt)
    object HUE : Property<Int>(name = "hue", parse = String::toInt)
    object SAT : Property<Int>(name = "sat", parse = String::toInt)
    object ColorMode : Property<LightMode>(name = "color_mode", parse = String::toLightMode)
    object Flowing : Property<Boolean>(name = "flowing", parse = String::toBoolean)
    object DelayOFF : Property<Int>(name = "delayoff", parse = String::toInt)
    object MusicOn : Property<Boolean>(name = "music_on", parse = String::toBoolean)
    object Name : Property<String>(name = "name", parse = String::toString)
    object BackgroundPower : Property<Boolean>(name = "bg_power", parse = String::toBoolean)
    object BackgroundFlowing : Property<Boolean>(name = "bg_flowing", parse = String::toBoolean)
    object BackgroundFlowParams : Property<String>(name = "bg_flow_params", parse = String::toString)
    object BackgroundCT : Property<Int>(name = "bg_ct", parse = String::toInt)
    object BackgroundLightMode : Property<LightMode>(name = "bg_lmode", parse = String::toLightMode)
    object BackgroundBight : Property<Int>(name = "bg_bright", parse = String::toInt)
    object BackgroundRGB : Property<Int>(name = "bg_rgb", parse = String::toInt)
    object BackgroundHUE : Property<Int>(name = "bg_hue", parse = String::toInt)
    object BackgroundSAT : Property<Int>(name = "bg_sat", parse = String::toInt)
    object NightLightBrightness : Property<Int>(name = "nl_br", parse = String::toInt)
    object ActiveMode : Property<Boolean>(name = "active_mode", parse = String::toBoolean)
}

data class Properties(private val map: Map<String, Any>) {
    operator fun <T> get(property: Property<T>): T? = map[property.name]?.let(Any::toString)?.let(property.parse)
}

fun <V> Flow<Properties>.filter(property: Property<V>): Flow<V> =
    mapNotNull { it[property] }