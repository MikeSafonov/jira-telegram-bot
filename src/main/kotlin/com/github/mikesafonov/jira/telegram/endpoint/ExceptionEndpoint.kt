package com.github.mikesafonov.jira.telegram.endpoint

import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionEndpoint{

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handle(ex : HttpMessageNotReadableException){
        println(ex)
    }
}