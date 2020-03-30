package com.github.mikesafonov.jira.telegram

import io.kotest.properties.Gen
import kotlin.random.Random

internal fun Gen<String>.empty(): String {
    return ""
}

internal fun Gen<String>.notBlank(): String {
    var value = random().first()
    while (value.isBlank()) {
        value = random().first()
    }
    return value
}

internal fun Random.positive(): Long {
    return nextLong(1, Long.MAX_VALUE)
}

internal fun Random.negative(): Long {
    return nextLong(Long.MIN_VALUE, 0)
}
