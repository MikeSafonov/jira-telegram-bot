package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.conditional.ConditionalOnJiraOAuth
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.jira.JiraApiService
import com.github.mikesafonov.jira.telegram.service.parameters.ParametersBuilderService
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramClient
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.templates.TemplateResolverService
import org.springframework.stereotype.Service

@Service
@ConditionalOnJiraOAuth
class GetIssueDescriptionTelegramCommandHandler(
    private val jiraApiService: JiraApiService,
    private val templateResolverService: TemplateResolverService,
    private val parametersBuilderService: ParametersBuilderService,
    telegramClient: TelegramClient
) : BaseCommandHandler(telegramClient), ArgParser {

    private val commandPrefix = "/description"

    override fun isHandle(command: TelegramCommand): Boolean {
        return command.isInState(State.INIT) && command.isStartsWithText(commandPrefix)
    }

    override fun handle(command: TelegramCommand): State {
        val id = command.chatId
        val commandArgs = getCommandArgs(command.text!!)
        if (commandArgs.size < 2) {
            telegramClient.sendTextMessage(id, "Wrong command syntax\n Should be: $commandPrefix <issue key>")
        } else {
            val issueKey = commandArgs[1]
            val issue = jiraApiService.getDescription(id, issueKey)
            if (issue == null) {
                telegramClient.sendTextMessage(id, "Issue $issueKey not found")
            } else {
//                val rawTemplate =
//                    templateResolverService.resolve(issue!!, parametersBuilderService.buildTemplateParameters(issue!!))
//                if(rawTemplate == null){
//                    com.github.mikesafonov.jira.telegram.service.logger.debug{"No template for event $event was found"}
//                } else{
//                    val compiledTemplate = templateService.buildMessage(rawTemplate)
//                    sendMessagesToTelegram(destinationLogins, compiledTemplate)
//                }
            }
        }
        return State.INIT
    }

}
