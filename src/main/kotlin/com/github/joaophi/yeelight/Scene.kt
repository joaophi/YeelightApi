package com.github.joaophi.yeelight

import com.github.joaophi.yeelight.CFAction
import com.github.joaophi.yeelight.OnCFFinish

sealed class Scene(val clazz: String, val val1: Int, val val2: Int, val val3: String? = null) {
    data class AutoDelayOff(val brightness: Int, val timer: Int) : Scene(clazz = "auto_delay_off", brightness, timer)
    data class Color(val color: Int, val brightness: Int) : Scene(clazz = "color", color, brightness)
    data class HSV(val color: Int, val brightness: Int) : Scene(clazz = "hsv", color, brightness)
    data class CT(val ct: Int, val brightness: Int) : Scene(clazz = "ct", ct, brightness)
    data class CF(val count: Int = 0, val onFinish: OnCFFinish = OnCFFinish.KEEP_STATE, val actions: List<CFAction>) :
        Scene(clazz = "cf", count, onFinish.action, actions.toFlowExpression()) {
        constructor(count: Int = 0, onFinish: OnCFFinish = OnCFFinish.KEEP_STATE, vararg actions: CFAction) :
                this(count, onFinish, actions.toList())
    }
}