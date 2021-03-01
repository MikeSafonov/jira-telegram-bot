package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.ChatService
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramClient
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

/**
 * @author Mike Safonov
 */
@Service
class RemoveUserTelegramCommandHandler(
    botProperties: BotProperties,
    private val chatService: ChatService,
    telegramClient: TelegramClient
) : AdminCommandTelegramCommandHandler(botProperties, telegramClient) {

    private val commandPrefix = "/remove_user"

    override fun isHandle(command: TelegramCommand): Boolean {
        return super.isHandle(command) && command.isInState(State.INIT) && command.isStartsWithText(commandPrefix)
    }

    override fun handle(command: TelegramCommand): State {
        val id = command.chatId
        val commandArgs = getCommandArgs(command.text!!)
        if (commandArgs.size < 2) {
            telegramClient.sendTextMessage(id, "Wrong command syntax\n Should be: $commandPrefix <jiraLogin>")
        } else {
            try {
                val jiraLogin = commandArgs[1]
                chatService.deleteByJiraId(jiraLogin)
                telegramClient.sendTextMessage(id, "User $jiraLogin was removed successfully")
            } catch (e: Exception) {
                logger.error(e.message, e)
                telegramClient.sendTextMessage(id, "Unexpected error")
            }
        }

        return State.INIT
    }

    /**
     * Method collects command arguments from full command text [message]
     * @param message text command
     * @return list of command arguments
     */
    private fun getCommandArgs(message: String): List<String> {
        return message.split(" ")
    }


}
