package com.github.mikesafonov.jira.telegram.config

import com.github.mikesafonov.jira.telegram.service.destination.DefaultDestinationDetectorService
import com.github.mikesafonov.jira.telegram.service.destination.DestinationDetectorService
import com.github.mikesafonov.jira.telegram.service.parameters.DefaultParametersBuilderService
import com.github.mikesafonov.jira.telegram.service.parameters.ParametersBuilderService
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.bots.DefaultBotOptions

/**
 * @author Mike Safonov
 */
@Configuration
class ApplicationConfiguration {

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

    @Bean
    @ConditionalOnMissingBean(DestinationDetectorService::class)
    fun defaultDestinationDetectorService(jiraBotProperties: JiraBotProperties) : DefaultDestinationDetectorService {
        return DefaultDestinationDetectorService(jiraBotProperties)
    }

    @Bean
    @ConditionalOnMissingBean(ParametersBuilderService::class)
    fun defaultParametersBuilderService(jiraBotProperties: JiraBotProperties) : DefaultParametersBuilderService {
        return DefaultParametersBuilderService(jiraBotProperties)
    }

}