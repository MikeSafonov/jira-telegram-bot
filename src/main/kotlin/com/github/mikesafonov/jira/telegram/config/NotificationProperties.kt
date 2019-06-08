package com.github.mikesafonov.jira.telegram.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author Mike Safonov
 */
@Component
@ConfigurationProperties("jira.bot.notification")
class NotificationProperties {

    /**
     * jira instance url for building browse link in notification message
     */
    var jiraUrl : String = ""
    /**
     * is events fired by me will be sent to me
     */
    var sendToMe : Boolean = false

}