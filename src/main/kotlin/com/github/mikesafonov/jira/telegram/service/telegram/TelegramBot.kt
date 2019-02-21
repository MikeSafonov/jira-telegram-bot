package com.github.mikesafonov.jira.telegram.service.telegram

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
class TelegramBot(
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

    fun sendMarkdownMessage(user: Long, message: String) {
        SendMessage().apply {
            chatId = user.toString()
            text = message
            enableMarkdown(true)
        }.also { execute(it) }
    }

    fun sendTextMessage(user: String, message: String) {
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