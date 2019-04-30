package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.service.jira.JiraAuthService
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.JiraAuthTelegramCommandHandler
import io.kotlintest.specs.BehaviorSpec
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class JiraAuthTelegramCommandHandlerSpec : BehaviorSpec({
    val jiraAuthService = mockk<JiraAuthService>()
    var handler = JiraAuthTelegramCommandHandler(jiraAuthService, TelegramMessageBuilder())
})