package com.github.mikesafonov.jira.telegram

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("telegram.bot")
class BotProperties{
    lateinit var token: String
    lateinit var name: String
}