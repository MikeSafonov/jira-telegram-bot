package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.dao.TemplateParseMode
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.methods.ParseMode

/**
 * @author Mike Safonov
 */
@Service
class TelegramClient(
    private val sender: DefaultAbsSender,
    private val telegramMessageBuilder: TelegramMessageBuilder
) {

    fun sendTextMessage(user: Long, message: String) {
        telegramMessageBuilder.createMessages(user, message).forEach { sender.execute(it) }
    }

    fun sendMarkdownMessage(user: Long, message: String) {
        telegramMessageBuilder.createMarkdownMessages(user, message).forEach { sender.execute(it) }
    }

    fun sendDeleteMessage(user : Long, idMessage: Int){
        telegramMessageBuilder.createDeleteMessage(user, idMessage).also { sender.execute(it) }
    }

    fun sendMessage(user: Long, message: String, parseMode: TemplateParseMode) {
        val telegramParseMode = toTelegramParseMode(parseMode)
        telegramMessageBuilder.createMessages(user, message).forEach {
            it.parseMode = telegramParseMode
            sender.execute(it)
        }
    }

    private fun toTelegramParseMode(parseMode: TemplateParseMode) : String {
        return when(parseMode) {
            TemplateParseMode.MARKDOWN -> ParseMode.MARKDOWN
            TemplateParseMode.MARKDOWN_V2 -> ParseMode.MARKDOWNV2
            TemplateParseMode.HTML -> ParseMode.HTML
        }
    }
}
