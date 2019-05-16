package com.github.mikesafonov.jira.telegram.config.prometheus

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Service

/**
 * @author Mike Safonov
 */
@Service
class JiraEventCounter(meterRegistry: MeterRegistry) {

    private val errorCounter: Counter = Counter.builder("jira.bot.event.error.counter").register(meterRegistry)
    private val eventCounter: Counter = Counter.builder("jira.bot.event.counter").register(meterRegistry)

    fun incrementError(){
        errorCounter.increment()
    }

    fun incrementEvent(){
        eventCounter.increment()
    }
}