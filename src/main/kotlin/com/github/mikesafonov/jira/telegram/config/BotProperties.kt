package com.github.mikesafonov.jira.telegram.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author Mike Safonov
 */
@Component
@ConfigurationProperties("telegram.bot")
class BotProperties {
    /**
     * telegram bot secret token
     */
    lateinit var token: String
    /**
     * telegram bot name
     */
    lateinit var name: String

    /**
     * id of telegram bot admin
     */
    var adminId: Long? = null

    /**
     * telegram bot http proxy host
     */
    var proxyHost: String? = null
    /**
     * telegram bot http proxy port
     */
    var proxyPort: Int? = null

    /**
     * timeout in milliseconds until a connection is established
     */
    var connectionTimeout: Int = -1

    /**
     * timeout in milliseconds used when requesting a connection
     */
    var connectionRequestTimeout: Int = -1
    /**
     * the socket timeout in milliseconds, which is the timeout for waiting for data  or, put differently,
     * a maximum period inactivity between two consecutive data packets)
     */
    var socketTimeout: Int = -1

    val isProxy: Boolean
        get() {
            return proxyHost != null && proxyPort != null
        }
}