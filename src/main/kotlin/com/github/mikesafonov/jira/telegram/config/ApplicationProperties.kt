package com.github.mikesafonov.jira.telegram.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author Mike Safonov
 */
@Component
@ConfigurationProperties("jira.bot")
class ApplicationProperties {

    var notification: NotificationProperties = NotificationProperties()

}