package com.github.mikesafonov.jira.telegram.endpoint

import mu.KotlinLogging
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger {}

/**
 * @author Mike Safonov
 */
@RestControllerAdvice
class ExceptionEndpoint{

    @ExceptionHandler(Exception::class)
    fun handle(ex : Exception){
        logger.error(ex.message, ex)
    }
}