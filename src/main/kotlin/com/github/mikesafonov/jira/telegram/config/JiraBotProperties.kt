package com.github.mikesafonov.jira.telegram.config

import com.github.mikesafonov.jira.telegram.service.templates.TemplateType
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author Mike Safonov
 */
@Component
@ConfigurationProperties("jira.bot")
class JiraBotProperties {

    /**
     * type of supported template engine
     */
    var templateType : TemplateType = TemplateType.FREEMARKER

    var notification: NotificationProperties = NotificationProperties()

}