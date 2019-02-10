package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.BotProperties
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.BotOptions
import org.telegram.telegrambots.meta.generics.LongPollingBot

@Service
class TelegramBotService (val botProperties: BotProperties) : LongPollingBot {
    override fun getBotToken(): String {
        return botProperties.token
    }

    override fun onUpdateReceived(update: Update?) {
        if(update != null){
            onUpdate(update)
        }
    }

    private fun onUpdate(update: Update){

    }

    override fun getBotUsername(): String {
        return botProperties.name
    }

    override fun getOptions(): BotOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearWebhook() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}