package com.github.mikesafonov.jira.telegram.config.conditional

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty

/**
 * @author Mike Safonov
 */

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ConditionalOnProperty(
    prefix = "jira.watchers", name = ["username", "password"], matchIfMissing = false
)
annotation class ConditionalOnJiraWatchers
