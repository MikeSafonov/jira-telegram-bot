package com.github.mikesafonov.jira.telegram

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.PropertySource
import org.telegram.telegrambots.ApiContextInitializer

/**
 * @author Mike Safonov
 */
@EnableConfigurationProperties
@SpringBootApplication
@PropertySource(value = ["classpath:META-INF/build-info.properties"], ignoreResourceNotFound = true)
class Application

    fun main(args:Array<String>) {
        ApiContextInitializer.init()
        runApplication<Application>(*args)
    }
