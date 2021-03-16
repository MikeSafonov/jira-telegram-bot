package com.github.mikesafonov.jira.telegram.service.templates

import com.github.mikesafonov.jira.telegram.dao.TemplateRepository
import com.github.mikesafonov.jira.telegram.dto.Event
import org.springframework.stereotype.Service

@Service
class TemplateResolverService(private val templateRepository: TemplateRepository) {

    fun resolve(event: Event, parameters: Map<String, Any>): RawTemplate? {
        val issueEventTypeName = event.issueEventTypeName
        if (issueEventTypeName != null) {
            val templateKey = issueEventTypeName.name.toLowerCase()
            return templateRepository.findByKey(templateKey)?.let {
                RawTemplate(templateKey, it.template, parameters)
            }
        }
        return null
    }

    fun resolve(parameters: Map<String, Any>): RawTemplate? {
        return templateRepository.findByKey("issue")?.let {
            RawTemplate("issue", it.template, parameters)
        }
    }
}
