package com.github.mikesafonov.jira.telegram.config.conditional

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty

/**
 * @author Mike Safonov
 */

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@ConditionalOnProperty(
    prefix = "jira.oauth", name = ["baseUrl", "authorizationUrl", "accessTokenUrl",
        "requestTokenUrl", "consumerKey", "publicKey", "privateKey"], matchIfMissing = false
)
annotation class ConditionalOnJiraOAuth