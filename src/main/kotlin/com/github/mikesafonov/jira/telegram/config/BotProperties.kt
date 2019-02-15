package com.github.mikesafonov.jira.telegram.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("telegram.bot")
class BotProperties{
    /**
     * telegram bot secret token
     */
    lateinit var token: String
    /**
     * telegram bot name
     */
    lateinit var name: String

    /**
     * telegram bot http proxy host
     */
    var proxyHost : String? = null
    /**
     * telegram bot http proxy port
     */
    var proxyPort : Int? = null
}