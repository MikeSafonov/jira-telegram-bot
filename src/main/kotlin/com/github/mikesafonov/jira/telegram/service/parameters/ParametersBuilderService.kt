package com.github.mikesafonov.jira.telegram.service.parameters

import com.github.mikesafonov.jira.telegram.dto.Event

/**
 * Interface for collecting input parameters for message template
 * @author Mike Safonov
 */

interface ParametersBuilderService {

    fun buildTemplateParameters(event: Event): Map<String, Any>
}