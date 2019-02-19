package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

private val logger = KotlinLogging.logger {}

/**
 * Service for working with telegram bot api
 * @author Mike Safonov
 */
@Service
class TelegramBotService(
    private val botProperties: BotProperties,
    private val chatRepository: ChatRepository,
    options: DefaultBotOptions?
) :
    TelegramLongPollingBot(options) {


    override fun getBotToken(): String {
        return botProperties.token
    }

    override fun onUpdateReceived(update: Update?) {
        update?.let {
            onUpdate(it)
        }
    }

    /**
     * Send telegram message to jira login [jiraLogin] user. If no chat id for this
     * [jiraLogin] no message will be sended
     * @param jiraLogin user jira login
     * @param telegram message markdown text
     */
    fun sendMessage(jiraLogin: String, telegramMessage: String) {
        chatRepository.findByJiraId(jiraLogin)?.let {
            sendMessageToUser(it.telegramId, telegramMessage)
        }
    }

    /**
     * Create telegram message with markdown syntax and send via telegram bot to user with id [user]
     * @param user telegram user id
     * @param messageText markdown text
     */
    fun sendMessageToUser(user: Long, messageText: String) {
        val message = SendMessage()
        message.chatId = user.toString()
        message.text = messageText
        message.enableMarkdown(true)
        execute(message)
    }

    /**
     * this method simply log received [update]
     */
    private fun onUpdate(update: Update) {
        logger.info(update.toString())
    }

    override fun getBotUsername(): String {
        return botProperties.name
    }

}