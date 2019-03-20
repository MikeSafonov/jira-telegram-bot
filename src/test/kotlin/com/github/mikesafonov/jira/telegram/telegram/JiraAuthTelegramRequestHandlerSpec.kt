package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.service.jira.JiraAuthService
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.JiraAuthTelegramRequestHandler
import io.kotlintest.specs.BehaviorSpec
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class JiraAuthTelegramRequestHandlerSpec : BehaviorSpec({
    val chatRepository = mockk<ChatRepository>()
    val jiraAuthService = mockk<JiraAuthService>()
    var handler = JiraAuthTelegramRequestHandler(chatRepository, jiraAuthService)
})