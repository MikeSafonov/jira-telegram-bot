package com.github.mikesafonov.jira.telegram.endpoint

import mu.KotlinLogging
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class ExceptionEndpoint{

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handle(ex : HttpMessageNotReadableException){
        logger.error(ex.message, ex)
    }
}