package com.github.joaophi.yeelight

enum class CronType(val value: Int) {
    POWER_OFF(value = 0),
    ;
}

data class Cron(
    val type: CronType,
    val delay: Int,
    val mix: Int,
) {
    constructor(map: Map<String, Double>) : this(
        type = map["type"]?.toInt()?.toCronType() ?: throw Exception("cron type not found"),
        delay = map["delay"]?.toInt() ?: throw Exception("cron delay not found"),
        mix = map["mix"]?.toInt() ?: throw Exception("cron mix not found"),
    )
}
