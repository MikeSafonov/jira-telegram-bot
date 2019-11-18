package com.github.mikesafonov.jira.telegram.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("jira.watchers")
class JiraWatchersProperties {
    var username: String = ""
    var password: String = ""
}
