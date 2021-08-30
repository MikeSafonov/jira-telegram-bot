package com.github.mikesafonov.jira.telegram.service.templates

import com.github.mikesafonov.jira.telegram.dao.TemplateParseMode

/**
 * Compiled and processed template. Contains ready to send telegram message [message] in
 * markdown style if flag [markdown] is *true*
 * @author Mike Safonov
 */
data class CompiledTemplate(val message: String, val markdown: Boolean)

data class RawTemplate(val templateKey: String, val template: String, val parameters: Map<String, Any>,
                       val parseMode: TemplateParseMode)
