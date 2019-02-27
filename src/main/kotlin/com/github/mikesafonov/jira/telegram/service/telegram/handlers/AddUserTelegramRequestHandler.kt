package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message


private val logger = KotlinLogging.logger {}

/**
 * @author Mike Safonov
 */
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
        val commandArgs = getCommandArgs(message.text)
        if (commandArgs.size < 3) {
            return createMessage(id, "Wrong command syntax: Should be: /add_user <jiraLogin> <telegramId>")
        } else {
            return try {
                val jiraLogin = validateJiraLogin(commandArgs[1])
                val telegramId = validateTelegramId(commandArgs[2].toLong())
                addNewChat(jiraLogin, telegramId)
                createMessage(id, "Jira user $jiraLogin with telegram id $telegramId was added successfully")
            } catch (e: NumberFormatException) {
                createMessage(id, "Wrong command args: telegramId must be a positive number")
            } catch (e: AddUserBotException) {
                createMessage(id, e.message ?: "")
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

    /**
     * Searching in database chat with [jiraId] jira login.
     * @param jiraId jira login
     * @return true if [jiraId] exist in database
     */
    private fun isJiraUserExist(jiraId: String): Boolean {
        return chatRepository.findByJiraId(jiraId) != null
    }

    /**
     * Searching in database chat with [telegramId] telegram chat id.
     * @param telegramId telegram chat id
     * @return true if [telegramId] exist in database
     */
    private fun isTelegramUserExist(telegramId: Long): Boolean {
        return chatRepository.findByTelegramId(telegramId) != null
    }

    /**
     * Validates [jiraLogin] : check if login not blank and not exist in database
     * @param jiraLogin jira login
     * @return [jiraLogin] back if check success
     * @throws AddUserBotException
     */
    private fun validateJiraLogin(jiraLogin: String): String {
        if (jiraLogin.isBlank()) {
            throw AddUserBotException("Wrong command args: jiraLogin must not be blank")
        }

        if (isJiraUserExist(jiraLogin)) {
            throw AddUserBotException("Jira login $jiraLogin already exist")
        }

        return jiraLogin
    }

    /**
     * Validates [telegramId] : check if chat id non negative and not exist in database
     * @param telegramId telegram chat id
     * @return [telegramId] back if check success
     * @throws AddUserBotException
     */
    private fun validateTelegramId(telegramId: Long): Long {
        if (telegramId < 0) {
            throw AddUserBotException("Wrong command args: telegramId must be a positive number")
        }

        if (isTelegramUserExist(telegramId)) {
            throw AddUserBotException("Telegram id $telegramId already exist")
        }

        return telegramId
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