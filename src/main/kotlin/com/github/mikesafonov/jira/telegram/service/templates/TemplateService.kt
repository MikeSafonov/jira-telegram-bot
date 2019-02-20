package com.github.mikesafonov.jira.telegram.service.templates

import com.github.mikesafonov.jira.telegram.dto.Event

/**
 * Interface for building telegram message for specific [Event] by compiling and processing template
 *
 * @author Mike Safonov
 */
interface TemplateService {

    /**
     * Find template for [event], compile and process with input parameters [parameters]
     *
     * @param event jira event
     * @param parameters input parameters for template
     * @return processed message or null if template for type [issueEventTypeName] not exist
     */
    fun buildMessage(event: Event, parameters: Map<String, Any>): CompiledTemplate?
}