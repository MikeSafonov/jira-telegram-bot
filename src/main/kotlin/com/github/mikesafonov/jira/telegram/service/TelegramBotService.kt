package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.service.templates.CompiledTemplate
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

    override fun getBotUsername(): String {
        return botProperties.name
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
    fun sendMessage(jiraLogin: String, template: CompiledTemplate) {
        chatRepository.findByJiraId(jiraLogin)?.let {
            sendMessageToUser(it.telegramId, template)
        }
    }

    /**
     * Create telegram message with markdown syntax and send via telegram bot to user with id [user]
     * @param user telegram user id
     * @param messageText markdown text
     */
    private fun sendMessageToUser(user: Long, template: CompiledTemplate) {
        val message = SendMessage().apply {
            chatId = user.toString()
            text = template.message
            // TODO: markdown only at the moment
            enableMarkdown(true)
        }
        execute(message)
    }

    private fun sendTextMessage(user: String, message: String) {
        SendMessage().apply {
            chatId = user
            text = message
        }.also { execute(it) }
    }

    /**
     * process [update]
     */
    private fun onUpdate(update: Update) {
        if (update.hasMessage() && update.message.hasText()) {
            when (update.message.text) {
                "/me" -> {
                    val chatId = update.message.chatId.toString()
                    sendTextMessage(chatId, "Your chat id: $chatId")
                }

                "/jira_login" -> {
                    val chatId = update.message.chatId
                    val jiraId = chatRepository.findByTelegramId(chatId)?.jiraId
                    if (jiraId == null) {
                        sendTextMessage(
                            chatId.toString(),
                            "You not registered at this bot yet. Please contact your system administrator for registration."
                        )
                    } else {
                        sendTextMessage(chatId.toString(), "Your jira login: $jiraId")
                    }
                }
                "/help" -> {
                    val chatId = update.message.chatId.toString()
                    val helpMessage = """This is jira-telegram-bot. Supported commands:
/me - prints telegram chat id
/jira_login - prints attached jira login to this telegram chat id
/help - prints help message
                    """.trimMargin()
                    sendTextMessage(chatId, helpMessage)
                }
                else -> {
                    sendTextMessage(update.message.chatId.toString(), "Unknown command. Try /help command")
                }

            }
        }
        logger.debug(update.toString())
    }
}