package com.github.mikesafonov.jira.telegram

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.telegram.telegrambots.ApiContextInitializer

/**
 * @author Mike Safonov
 */
@EnableConfigurationProperties
@SpringBootApplication
class Application

    fun main(args:Array<String>) {
        ApiContextInitializer.init()
        runApplication<Application>(*args)
    }
