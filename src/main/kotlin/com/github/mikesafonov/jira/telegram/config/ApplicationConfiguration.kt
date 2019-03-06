package com.github.mikesafonov.jira.telegram.config

import com.github.mikesafonov.jira.telegram.service.destination.DefaultDestinationDetectorService
import com.github.mikesafonov.jira.telegram.service.destination.DestinationDetectorService
import com.github.mikesafonov.jira.telegram.service.parameters.DefaultParametersBuilderService
import com.github.mikesafonov.jira.telegram.service.parameters.ParametersBuilderService
import org.apache.http.HttpHost
import org.apache.http.client.config.RequestConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CommonsRequestLoggingFilter
import org.telegram.telegrambots.bots.DefaultBotOptions

/**
 * @author Mike Safonov
 */
@Configuration
class ApplicationConfiguration {

    @Bean
    fun botOptions(botProperties: BotProperties): DefaultBotOptions {
        val botOptions = DefaultBotOptions()
        val requestConfigBuilder = RequestConfig.custom()
            .setSocketTimeout(botProperties.socketTimeout)
            .setConnectionRequestTimeout(botProperties.connectionRequestTimeout)
            .setConnectTimeout(botProperties.connectionTimeout)
        if (botProperties.isProxy) {
            val proxy = HttpHost(botProperties.proxyHost, botProperties.proxyPort!!)
            botOptions.requestConfig = requestConfigBuilder.setProxy(proxy).build()
        } else {
            botOptions.requestConfig = requestConfigBuilder.build()
        }
        return botOptions
    }


    @Bean
    @ConditionalOnMissingBean(DestinationDetectorService::class)
    fun defaultDestinationDetectorService(applicationProperties: ApplicationProperties): DefaultDestinationDetectorService {
        return DefaultDestinationDetectorService(applicationProperties)
    }

    @Bean
    @ConditionalOnMissingBean(ParametersBuilderService::class)
    fun defaultParametersBuilderService(applicationProperties: ApplicationProperties): DefaultParametersBuilderService {
        return DefaultParametersBuilderService(applicationProperties)
    }

    @Bean
    fun logRequestFilter(@Value("\${request.logging.payload.length}") payloadLength: Int): CommonsRequestLoggingFilter {
        val filter = CommonsRequestLoggingFilter()
        filter.setIncludePayload(true)
        filter.setAfterMessagePrefix("REQUEST DATA : ")
        filter.setIncludeHeaders(false)
        filter.setMaxPayloadLength(payloadLength)
        filter.setIncludeQueryString(false)
        return filter
    }

}