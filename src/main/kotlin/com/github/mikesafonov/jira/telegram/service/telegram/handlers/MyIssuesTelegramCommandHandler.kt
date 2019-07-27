package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.atlassian.jira.rest.client.api.domain.Issue
import com.github.mikesafonov.jira.telegram.config.conditional.ConditionalOnJiraOAuth
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.jira.JiraApiService
import com.github.mikesafonov.jira.telegram.service.jira.JiraIssueBrowseLinkService
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramClient
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import org.springframework.stereotype.Service

@Service
@ConditionalOnJiraOAuth
class MyIssuesTelegramCommandHandler(
    private val jiraApiService: JiraApiService,
    telegramClient: TelegramClient,
    private val jiraIssueBrowseLinkService: JiraIssueBrowseLinkService
) : BaseCommandHandler(telegramClient) {

    override fun isHandle(command: TelegramCommand): Boolean {
        return command.isInState(State.INIT) && command.isMatchText("/my_issues")
                && command.authorization != null
    }

    override fun handle(command: TelegramCommand): State {
        val jiraId = command.chat!!.jiraId
        val telegramId = command.chat.telegramId
        val myIssues = jiraApiService.getMyIssues(telegramId, jiraId)
        telegramClient.sendMarkdownMessage(telegramId, buildMessage(myIssues))
        return State.INIT
    }

    private fun buildMessage(issues : Iterable<Issue>) : String{
        val joinedIssues = issues.joinToString(separator = "\n") {
            val issueLink = jiraIssueBrowseLinkService.createBrowseLink(it.key, it.self.toString())
            "[${it.key}]($issueLink) ${it.summary}"
        }
        if(joinedIssues.isBlank() || joinedIssues.isEmpty()){
            return "No unresolved issues was found"
        }
        return joinedIssues
    }
}