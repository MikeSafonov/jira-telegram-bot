package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

private val logger = KotlinLogging.logger {}

/**
 * @author Mike Safonov
 */
@Service
class RemoveUserTelegramRequestHandler(
    botProperties: BotProperties,
    private val chatRepository: ChatRepository
) : AdminCommandTelegramRequestHandler(botProperties) {

    private val commandPrefix = "/remove_user"

    override fun isHandle(message: Message): Boolean {
        return isAdminUser(message) && message.text.startsWith(commandPrefix)
    }

    override fun handle(message: Message): BotApiMethod<Message> {
        val id = message.chatId.toString()
        val commandArgs = getCommandArgs(message.text)
        if (commandArgs.size < 2) {
            return createMessage(id, "Wrong command syntax: Should be: $commandPrefix <jiraLogin>")
        } else {
            return try {
                val jiraLogin = commandArgs[1]
                chatRepository.deleteByJiraId(jiraLogin)
                return createMessage(id, "User $jiraLogin was removed successfully")
            } catch (e: Exception) {
                logger.error(e.message, e)
                createMessage(id, "Unexpected error")
            }
        }
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