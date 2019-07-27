package com.github.mikesafonov.jira.telegram.service.jira

import com.github.mikesafonov.jira.telegram.config.ApplicationProperties
import com.github.mikesafonov.jira.telegram.config.NotificationProperties
import org.springframework.stereotype.Service

/**
 * @author Mike Safonov
 */
@Service
class JiraIssueBrowseLinkService(private val applicationProperties: ApplicationProperties) {

    /**
     * Build issue link by concatenation of [NotificationProperties.jiraUrl] and [key]. Returning
     * [self] if [NotificationProperties.jiraUrl] is `null`
     * @param key jira issues key
     * @param self jira self link
     * @return link to browse issue
     */
    fun createBrowseLink(key: String?, self: String?): String {
        val notificationProperties = applicationProperties.notification
        if (notificationProperties.jiraUrl.isNotBlank()) {
            val issueKey = key ?: ""
            return if (notificationProperties.jiraUrl.endsWith("/")) {
                "${notificationProperties.jiraUrl}browse/$issueKey"
            } else {
                "${notificationProperties.jiraUrl}/browse/$issueKey"
            }
        }
        return self ?: ""
    }
}