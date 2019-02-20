package com.github.mikesafonov.jira.telegram.service.templates

/**
 * Compiled and processed template. Contains ready to send telegram message [message] in
 * markdown style if flag [markdown] is *true*
 * @author Mike Safonov
 */
data class CompiledTemplate(val message: String, val markdown: Boolean)