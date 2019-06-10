package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.AddChatException
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
class AddUserTelegramCommandHandler(
    private val chatService: ChatService,
    botProperties: BotProperties,
    telegramClient: TelegramClient
) :
    AdminCommandTelegramCommandHandler(botProperties, telegramClient) {

    private val commandPrefix = "/add_user"

    override fun isHandle(command: TelegramCommand): Boolean {
        return super.isHandle(command) && isInState(command, State.INIT) && isStartsWithText(command, commandPrefix)
    }

    override fun handle(command: TelegramCommand): State {
        val id = command.chatId
        val commandArgs = getCommandArgs(command.text!!)
        if (commandArgs.size < 3) {
            telegramClient.sendTextMessage(id, "Wrong command syntax\n Should be: $commandPrefix <jiraLogin> <telegramId>")
        } else {
            try {
                val jiraLogin = commandArgs[1]
                val telegramId = commandArgs[2].toLong()
                chatService.addNewChat(jiraLogin, telegramId)
                telegramClient.sendTextMessage(id, "Jira user $jiraLogin with telegram id $telegramId was added successfully")
            } catch (e: NumberFormatException) {
                telegramClient.sendTextMessage(id, "Wrong command args: telegramId must be a positive number")
            } catch (e: AddChatException) {
                telegramClient.sendTextMessage(id, e.message!!)
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