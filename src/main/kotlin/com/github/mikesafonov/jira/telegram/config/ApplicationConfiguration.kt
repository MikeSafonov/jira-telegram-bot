package com.github.mikesafonov.jira.telegram.config

import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import com.github.mikesafonov.jira.telegram.service.destination.DefaultDestinationDetectorService
import com.github.mikesafonov.jira.telegram.service.destination.DestinationDetectorService
import com.github.mikesafonov.jira.telegram.service.destination.WatchersDestinationDetectorService
import com.github.mikesafonov.jira.telegram.service.jira.JiraIssueBrowseLinkService
import com.github.mikesafonov.jira.telegram.service.jira.JiraWatchersLoader
import com.github.mikesafonov.jira.telegram.service.parameters.DefaultParametersBuilderService
import com.github.mikesafonov.jira.telegram.service.parameters.ParametersBuilderService
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramUpdateManager
import mu.KotlinLogging
import org.apache.http.HttpHost
import org.apache.http.client.config.RequestConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CommonsRequestLoggingFilter
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

private val logger = KotlinLogging.logger {}

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
        botOptions.requestConfig = if (botProperties.isProxy) {
            val proxy = HttpHost(botProperties.proxyHost, botProperties.proxyPort!!)
            requestConfigBuilder.setProxy(proxy).build()
        } else {
            requestConfigBuilder.build()
        }
        return botOptions
    }

    @Bean
    fun telegramSender(botOptions: DefaultBotOptions, botProperties: BotProperties): DefaultAbsSender {
        return object : DefaultAbsSender(botOptions) {
            override fun getBotToken(): String {
                return botProperties.token
            }

        }
    }

    @Bean
    fun telegramBot(
        telegramUpdateManager: TelegramUpdateManager, botOptions: DefaultBotOptions,
        botProperties: BotProperties
    ): Any {
        return object : TelegramLongPollingBot(botOptions) {
            override fun getBotUsername(): String {
                return botProperties.name
            }

            override fun getBotToken(): String {
                return botProperties.token
            }

            override fun onUpdateReceived(update: Update?) {
                update?.let {
                    telegramUpdateManager.onUpdate(it)
                }
            }
        }
    }

    @Bean
    fun destinationDetectorService(
        applicationProperties: ApplicationProperties,
        watchersLoader: JiraWatchersLoader?
    ): DestinationDetectorService {
        if (watchersLoader == null) {
            logger.info("created DefaultDestinationDetectorService")
            return DefaultDestinationDetectorService(applicationProperties)
        }
        logger.info("created WatchersDestinationDetectorService")
        return WatchersDestinationDetectorService(applicationProperties, watchersLoader)
    }

    @Bean
    @ConditionalOnMissingBean(ParametersBuilderService::class)
    fun defaultParametersBuilderService(jiraIssueBrowseLinkService: JiraIssueBrowseLinkService): DefaultParametersBuilderService {
        return DefaultParametersBuilderService(jiraIssueBrowseLinkService)
    }

    @Bean
    fun asynchronousJiraRestClientFactory(): AsynchronousJiraRestClientFactory {
        return AsynchronousJiraRestClientFactory()
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
