package com.github.mikesafonov.jira.telegram.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("jira.oauth")
class JiraOAuthProperties {
    var baseUrl: String = ""
    var privateKey: String = ""
    var consumerKey: String = ""
    var authorizationUrl: String = ""
    var accessTokenUrl: String = ""
    var requestTokenUrl: String = ""
}