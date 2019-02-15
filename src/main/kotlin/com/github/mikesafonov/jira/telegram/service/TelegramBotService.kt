package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.config.BotProperties
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

private val logger = KotlinLogging.logger {}

@Service
class TelegramBotService(private val botProperties: BotProperties, options: DefaultBotOptions?) :
    TelegramLongPollingBot(options) {


    override fun getBotToken(): String {
        return botProperties.token
    }

    override fun onUpdateReceived(update: Update?) {
        if (update != null) {
            onUpdate(update)
        }
    }

    fun sendMessageToUser(user: Long, messageText: String) {
        val message = SendMessage()
        message.chatId = user.toString()
        message.text = messageText
        message.enableMarkdown(true)
        execute(message)
    }

    private fun onUpdate(update: Update) {
        logger.info(update.toString())
    }

    override fun getBotUsername(): String {
        return botProperties.name
    }

}