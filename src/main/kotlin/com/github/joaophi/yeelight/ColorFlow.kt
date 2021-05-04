package com.github.joaophi.yeelight

enum class OnCFFinish(val action: Int) {
    REVERT_STATE(action = 0),
    KEEP_STATE(action = 1),
    POWER_OFF(action = 2);
}

sealed class CFAction(open val duration: Int, val mode: Int, open val value: Int, open val brightness: Int) {
    data class Color(override val duration: Int, override val value: Int, override val brightness: Int) :
        CFAction(duration, mode = 1, value, brightness)

    data class Temperature(override val duration: Int, override val value: Int, override val brightness: Int) :
        CFAction(duration, mode = 2, value, brightness)

    data class Sleep(override val duration: Int) : CFAction(duration, mode = 7, value = 0, brightness = 0)
}
