package com.github.mikesafonov.jira.telegram.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * @author Mike Safonov
 */
@Configuration
@ConfigurationProperties("notification")
class NotificationProperties {

    /**
     * jira instance url for building browse link in notification message
     */
    lateinit var jiraUrl : String
    /**
     * is events fired by me will be sended to me
     */
    var sendToMe : Boolean = false

}