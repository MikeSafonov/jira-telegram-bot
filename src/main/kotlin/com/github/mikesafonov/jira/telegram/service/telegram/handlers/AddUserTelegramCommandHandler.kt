package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.AddChatException
import com.github.mikesafonov.jira.telegram.service.ChatService
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommandResponse
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder
import mu.KotlinLogging
import org.springframework.stereotype.Service


private val logger = KotlinLogging.logger {}

/**
 * @author Mike Safonov
 */
@Service
class AddUserTelegramCommandHandler(
    botProperties: BotProperties,
    private val chatService: ChatService,
    private val telegramMessageBuilder: TelegramMessageBuilder
) :
    AdminCommandTelegramCommandHandler(botProperties) {

    private val commandPrefix = "/add_user"

    override fun isHandle(command: TelegramCommand): Boolean {
        return isAdminUser(command) && isInState(command, State.INIT) && isStartsWithText(command, commandPrefix)
    }

    override fun handle(command: TelegramCommand): TelegramCommandResponse {
        val id = command.chatId
        val commandArgs = getCommandArgs(command.text!!)
        val method = if (commandArgs.size < 3) {
            telegramMessageBuilder.createMessage(id, "Wrong command syntax\n Should be: $commandPrefix <jiraLogin> <telegramId>")
        } else {
            try {
                val jiraLogin = commandArgs[1]
                val telegramId = commandArgs[2].toLong()
                chatService.addNewChat(jiraLogin, telegramId)
                telegramMessageBuilder.createMessage(id, "Jira user $jiraLogin with telegram id $telegramId was added successfully")
            } catch (e: NumberFormatException) {
                telegramMessageBuilder.createMessage(id, "Wrong command args: telegramId must be a positive number")
            } catch (e: AddChatException) {
                telegramMessageBuilder.createMessage(id, e.message!!)
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