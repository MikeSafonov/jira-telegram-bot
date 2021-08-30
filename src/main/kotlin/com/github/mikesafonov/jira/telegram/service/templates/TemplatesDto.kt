package com.github.mikesafonov.jira.telegram.service.templates

import com.github.mikesafonov.jira.telegram.dao.TemplateParseMode

/**
 * Compiled and processed template. Contains ready to send telegram message [message] in
 * [parseMode] style
 * @author Mike Safonov
 */
data class CompiledTemplate(val message: String, val parseMode: TemplateParseMode)

data class RawTemplate(val templateKey: String, val template: String, val parameters: MutableMap<String, Any>,
                       val parseMode: TemplateParseMode)
