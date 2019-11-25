package com.github.mikesafonov.jira.telegram.endpoint

import mu.KotlinLogging
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException

private val logger = KotlinLogging.logger {}

/**
 * @author Mike Safonov
 */
@RestControllerAdvice
class ExceptionEndpoint {

    @ExceptionHandler(TelegramApiRequestException::class)
    fun handleTelegramApiException(ex: TelegramApiRequestException) {
        logger.error("Telegram response: ${ex.apiResponse}", ex)
    }

    @ExceptionHandler(Exception::class)
    fun handle(ex: Exception) {
        logger.error(ex.message, ex)
    }
}
