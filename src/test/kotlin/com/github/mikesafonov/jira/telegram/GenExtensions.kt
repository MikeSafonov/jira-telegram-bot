package com.github.mikesafonov.jira.telegram

import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import kotlin.random.Random

internal fun Arb<String>.empty(): String {
    return ""
}

internal fun Arb<String>.notBlank(): String {
    var value = next()
    while (value.isBlank()) {
        value = next()
    }
    return value
}

internal fun Random.positive(): Long {
    return nextLong(1, Long.MAX_VALUE)
}

internal fun Random.negative(): Long {
    return nextLong(Long.MIN_VALUE, 0)
}
