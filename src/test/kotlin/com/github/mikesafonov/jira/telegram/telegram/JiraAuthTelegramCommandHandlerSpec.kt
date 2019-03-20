package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.service.jira.JiraAuthService
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.JiraAuthTelegramCommandHandler
import io.kotlintest.specs.BehaviorSpec
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class JiraAuthTelegramCommandHandlerSpec : BehaviorSpec({
    val chatRepository = mockk<ChatRepository>()
    val jiraAuthService = mockk<JiraAuthService>()
    var handler = JiraAuthTelegramCommandHandler(chatRepository, jiraAuthService)
})