package com.github.mikesafonov.jira.telegram.service.parameters

import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.dto.Issue

/**
 * Interface for collecting input parameters for message template
 * @author Mike Safonov
 */

interface ParametersBuilderService {

    fun buildTemplateParameters(event: Event): Map<String, Any>

    fun buildTemplateParameters(issue: Issue): Map<String, Any>
}
