package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * @author Mike Safonov
 */
private val logger = KotlinLogging.logger {}

@Service
class AddUserTelegramRequestHandler(
    botProperties: BotProperties,
    private val chatRepository: ChatRepository
) :
    AdminCommandTelegramRequestHandler(botProperties) {

    private val commandPrefix = "/add_user"

    override fun isHandle(message: Message): Boolean {
        return isAdminUser(message) && message.text.startsWith(commandPrefix)
    }

    override fun handle(message: Message): BotApiMethod<Message> {
        val id = message.chatId.toString()
        val commandArgs = getCommandArgs(message)
        if (commandArgs.size < 2) {
            return createMessage(id, "Wrong command syntax: Should be: /add_user <jiraLogin> <telegramId>")
        } else {
            val jiraLogin = commandArgs[0]
            if (jiraLogin.isBlank()) {
                return createMessage(id, "Wrong command args: jiraLogin must not be blank")
            }

            if (isJiraUserExist(jiraLogin)) {
                return createMessage(id, "Jira login $jiraLogin already exist")
            }

            try {
                val telegramId = commandArgs[1].toLong()
                if (telegramId < 0) {
                    return createMessage(id, "Wrong command args: telegramId must be a positive number")
                }

                if (isTelegramUserExist(telegramId)) {
                    return createMessage(id, "Telegram id $telegramId already exist")
                }
                addNewChat(jiraLogin, telegramId)
                return createMessage(id, "Jira user $jiraLogin with telegram id $telegramId was added successfully")
            } catch (e: NumberFormatException) {
                return createMessage(id, "Wrong command args: telegramId must be a positive number")
            } catch (e: Exception) {
                logger.error(e.message, e)
                return createMessage(id, "Unexpected error")
            }
        }
    }

    private fun getCommandArgs(message: Message): List<String> {
        return message.text.replace(commandPrefix, "").split(" ")
    }

    private fun isJiraUserExist(jiraId: String): Boolean {
        return chatRepository.findByJiraId(jiraId) != null
    }

    private fun isTelegramUserExist(telegramId: Long): Boolean {
        return chatRepository.findByTelegramId(telegramId) != null
    }

    private fun createMessage(id: String, message: String): SendMessage {
        return SendMessage().apply {
            chatId = id
            text = message
        }
    }

    private fun addNewChat(jiraLogin: String, telegramId: Long) {
        val chat = Chat(null, jiraLogin, telegramId)
        chatRepository.save(chat)
    }

}