package com.github.mikesafonov.jira.telegram.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * @author Mike Safonov
 */
@Configuration
@ConfigurationProperties(prefix = "build")
class BuildInfo {
    var time: String? = null
    var artifact: String? = null
    var group: String? = null
    var name: String? = null
    var version: String? = "dev"
}