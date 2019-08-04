package com.github.mikesafonov.jira.telegram.service.templates

import com.github.mikesafonov.jira.telegram.dto.Event

/**
 * Interface for building telegram message for specific [Event] by compiling and processing template
 *
 * @author Mike Safonov
 */
interface TemplateService {

    /**
     * Compile [RawTemplate]
     *
     * @param rawTemplate raw template
     * @return compiled [RawTemplate]
     */
    fun buildMessage(rawTemplate: RawTemplate): CompiledTemplate
}