package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dao.State
import org.springframework.stereotype.Service

/**
 * @author Mike Safonov
 */

@Service
class ChatService(private val chatRepository: ChatRepository){

    fun addNewChat(jiraLogin: String, telegramId: Long) {
        val validatedJiraLogin = validateJiraLogin(jiraLogin)
        val validatedTelegramId = validateTelegramId(telegramId)

        val chat = Chat(null, validatedJiraLogin, validatedTelegramId, State.INIT)
        chatRepository.save(chat)
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
     * @throws AddChatException
     */
    private fun validateJiraLogin(jiraLogin: String): String {
        if (jiraLogin.isBlank()) {
            throw AddChatException("Wrong command args: jiraLogin must not be blank")
        }

        if (isJiraUserExist(jiraLogin)) {
            throw AddChatException("Jira login $jiraLogin already exist")
        }

        return jiraLogin
    }

    /**
     * Validates [telegramId] : check if chat id non negative and not exist in database
     * @param telegramId telegram chat id
     * @return [telegramId] back if check success
     * @throws AddChatException
     */
    private fun validateTelegramId(telegramId: Long): Long {
        if (telegramId < 0) {
            throw AddChatException("Wrong command args: telegramId must be a positive number")
        }

        if (isTelegramUserExist(telegramId)) {
            throw AddChatException("Telegram id $telegramId already exist")
        }

        return telegramId
    }

    /**
     * Searching in database chat with [jiraId] jira login.
     * @param jiraId jira login
     * @return true if [jiraId] exist in database
     */
    private fun isJiraUserExist(jiraId: String): Boolean {
        return chatRepository.findByJiraId(jiraId) != null
    }
}