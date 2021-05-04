package com.github.joaophi.yeelight

import com.github.joaophi.yeelight.CFAction
import com.github.joaophi.yeelight.OnCFFinish

sealed class Command<R : Any>(val method: String) {
    open val params: List<Any> get() = emptyList()

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    open fun parseResult(result: List<Any>): R = when (val first = result.firstOrNull()) {
        is String -> first == "ok"
        else -> throw Exception("cannot parse result: '$result'")
    } as R

    data class CronAdd(val type: CronType = CronType.POWER_OFF, val minutes: Int) :
        Command<Boolean>(method = "cron_add") {
        override val params: List<Any> get() = listOf(type.value, minutes)
    }

    data class CronGet(val type: CronType = CronType.POWER_OFF) : Command<List<Cron>>(method = "cron_get") {
        override val params: List<Any> get() = listOf(type.value)

        override fun parseResult(result: List<Any>): List<Cron> = result
            .filterIsInstance<Map<String, Double>>()
            .map(::Cron)
    }

    data class CronDel(val type: CronType = CronType.POWER_OFF) : Command<Boolean>(method = "cron_del") {
        override val params: List<Any> get() = listOf(type.value)
    }

    data class GetProp(val props: List<Property<*>>) : Command<Properties>(method = "get_prop") {
        constructor(vararg properties: Property<*>) : this(properties.toList())

        override val params: List<Any> get() = props.map(Property<*>::name)

        override fun parseResult(result: List<Any>) =
            Properties(props.map(Property<*>::name).zip(result).toMap())
    }

    data class SetBright(val brightness: Int, val effect: Effect) : Command<Boolean>(method = "set_bright") {
        override val params: List<Any> get() = listOf(brightness, effect.value, effect.duration)
    }

    object SetDefault : Command<Boolean>(method = "set_default")

    data class SetName(val name: String) : Command<Boolean>(method = "set_name") {
        override val params: List<Any> get() = listOf(name)
    }

    data class SetMusic(val mode: MusicMode) : Command<Boolean>(method = "set_music") {
        override val params: List<Any> get() = listOfNotNull(mode.action, mode.host, mode.port)
    }

    data class SetPower(val power: Boolean, val effect: Effect, val mode: PowerMode = PowerMode.NORMAL) :
        Command<Boolean>(method = "set_power") {
        override val params: List<Any> get() = listOf(power.toOnOff(), effect.value, effect.duration, mode.value)
    }

    data class SetScene(val scene: Scene) : Command<Boolean>(method = "set_scene") {
        override val params: List<Any> get() = listOfNotNull(scene.clazz, scene.val1, scene.val2, scene.val3)
    }

    data class StartCF(
        val count: Int = 0,
        val onFinish: OnCFFinish = OnCFFinish.KEEP_STATE,
        val actions: List<CFAction>,
    ) : Command<Boolean>(method = "start_cf") {
        constructor(count: Int = 0, onFinish: OnCFFinish = OnCFFinish.KEEP_STATE, vararg actions: CFAction) :
                this(count, onFinish, actions.toList())

        override val params: List<Any> get() = listOf(count, onFinish.action, actions.toFlowExpression())
    }

    object StopCF : Command<Boolean>(method = "stop_cf")

    object Toggle : Command<Boolean>(method = "toggle")
}