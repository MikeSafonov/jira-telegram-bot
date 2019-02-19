package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.dto.Issue
import com.github.mikesafonov.jira.telegram.dto.IssueEventTypeName
import freemarker.template.Template
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.io.StringWriter

private val logger = KotlinLogging.logger {}

@Service
class EventService(
    private val telegramBotService: TelegramBotService,
    private val templateRegistry: TemplateRegistry,
    private val chatRepository: ChatRepository
) {

    fun handle(event: Event) {
        logger.info { "$event" }
        if (event.isIssueEvent) {
            handleIssue(event)
        } else {
            logger.info { "Unknown event: $event" }
        }
    }

    private fun handleIssue(event: Event) {

        if(event.issueEventTypeName != null){
            val destinationLogins = findDestinationLogins(event)
            if (destinationLogins.isEmpty()) {
                return
            }

            templateRegistry.getByIssueType(event.issueEventTypeName)?.let {
                val telegramMessage = processTemplate(it, event)
                destinationLogins.forEach {
                    sendMessage(it, telegramMessage)
                }
            }
        }
    }

    private fun processTemplate(template: Template, event: Event): String {
        val sw = StringWriter()
        val params = mapOf("event" to event)
        template.process(params, sw)
        return sw.toString()
    }

    private fun sendMessage(jiraLogin: String, telegramMessage: String) {
        chatRepository.findByJiraId(jiraLogin)?.let {
            telegramBotService.sendMessageToUser(it.telegramId, telegramMessage)
        }
    }

    private fun findDestinationLogins(event: Event): Array<String> {

        when (event.issueEventTypeName) {
            IssueEventTypeName.ISSUE_COMMENTED -> {
                event.issue?.let {
                    val commentAuthor = event.comment?.author
                    return if (commentAuthor == null) {
                        creatorAndAssigneeNames(it)
                            .toTypedArray()
                    } else {
                        creatorAndAssigneeNames(it)
                            .filter { it != commentAuthor.name }
                            .toTypedArray()
                    }
                }
                return emptyArray()
            }
            IssueEventTypeName.ISSUE_CREATED -> {
                event.issue?.let {
                    return listOfNotNull(it.assigneeName)
                        .toTypedArray()
                }
                return emptyArray()
            }
            IssueEventTypeName.ISSUE_GENERIC -> {
                event.issue?.let {
                    return creatorAndAssigneeNames(it)
                        .toTypedArray()
                }
                return emptyArray()
            }
            IssueEventTypeName.ISSUE_UPDATED -> {
                event.issue?.let {
                    val updateAuthor = event.user
                    return if (updateAuthor == null) {
                        creatorAndAssigneeNames(it)
                            .toTypedArray()
                    } else {
                        creatorAndAssigneeNames(it)
                            .filter { it != updateAuthor.name }
                            .toTypedArray()
                    }
                }
                return emptyArray()
            }
            // TODO: check event dto
            IssueEventTypeName.ISSUE_COMMENT_EDITED -> {
                event.issue?.let {
                    return creatorAndAssigneeNames(it)
                        .toTypedArray()
                }
                return emptyArray()
            }
            // TODO: check event dto
            IssueEventTypeName.ISSUE_COMMENT_DELETED -> {
                event.issue?.let {
                    return creatorAndAssigneeNames(it)
                        .toTypedArray()
                }
                return emptyArray()
            }
            null -> return emptyArray()
        }
    }

    private fun creatorAndAssigneeNames(issue: Issue): List<String> {
        return listOfNotNull(issue.creatorName, issue.reporterName, issue.assigneeName)
            .distinct()
    }
}