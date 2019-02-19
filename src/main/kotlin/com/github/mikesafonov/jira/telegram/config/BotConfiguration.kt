package com.github.mikesafonov.jira.telegram.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.bots.DefaultBotOptions

/**
 * @author Mike Safonov
 */
@Configuration
class BotConfiguration {

    @Bean
    fun botOptions(botProperties: BotProperties): DefaultBotOptions {
        val botOptions = DefaultBotOptions()
        if (botProperties.proxyHost != null && botProperties.proxyPort != null) {
            botOptions.proxyPort = botProperties.proxyPort ?: 0
            botOptions.proxyHost = botProperties.proxyHost
            botOptions.proxyType = DefaultBotOptions.ProxyType.HTTP
        }
        return botOptions
    }

}