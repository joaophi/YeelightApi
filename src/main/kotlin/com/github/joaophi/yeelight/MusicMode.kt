package com.github.joaophi.yeelight

sealed class MusicMode(val action: Int, open val host: String? = null, open val port: Int? = null) {
    data class On(override val host: String, override val port: Int) : MusicMode(action = 1, host, port)
    object Off : MusicMode(action = 0)
}