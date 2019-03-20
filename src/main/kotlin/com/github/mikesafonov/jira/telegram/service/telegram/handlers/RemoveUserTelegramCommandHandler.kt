package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommandResponse
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

/**
 * @author Mike Safonov
 */
@Service
class RemoveUserTelegramCommandHandler(
    botProperties: BotProperties,
    private val chatRepository: ChatRepository,
    private val telegramMessageBuilder: TelegramMessageBuilder
) : AdminCommandTelegramCommandHandler(botProperties) {

    private val commandPrefix = "/remove_user"

    override fun isHandle(command: TelegramCommand): Boolean {
        return isAdminUser(command) && isInState(command, State.INIT) &&  isStartsWithText(command, commandPrefix)
    }

    @Transactional
    override fun handle(command: TelegramCommand): TelegramCommandResponse {
        val id = command.chatId
        val commandArgs = getCommandArgs(command.text!!)
        val method = if (commandArgs.size < 2) {
            telegramMessageBuilder.createMessage(id, "Wrong command syntax\n Should be: $commandPrefix <jiraLogin>")
        } else {
            try {
                val jiraLogin = commandArgs[1]
                chatRepository.deleteByJiraId(jiraLogin)
                telegramMessageBuilder.createMessage(id, "User $jiraLogin was removed successfully")
            } catch (e: Exception) {
                logger.error(e.message, e)
                telegramMessageBuilder.createMessage(id, "Unexpected error")
            }
        }

        return TelegramCommandResponse(method, State.INIT)
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