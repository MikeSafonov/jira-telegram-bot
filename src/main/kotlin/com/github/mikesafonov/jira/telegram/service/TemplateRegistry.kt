package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.dao.TemplateRepository
import com.github.mikesafonov.jira.telegram.dto.IssueEventTypeName
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import freemarker.template.Version
import mu.KotlinLogging
import no.api.freemarker.java8.Java8ObjectWrapper
import org.springframework.stereotype.Service
import java.io.StringReader


private val logger = KotlinLogging.logger {}

@Service
class TemplateRegistry(val templateRepository: TemplateRepository) {

    private val version = Version(2, 3, 20)
    private val cfg = Configuration(version)

    init {
        cfg.defaultEncoding = "UTF-8"
        cfg.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
        cfg.objectWrapper = Java8ObjectWrapper(version)
    }

    fun getByIssueType(issueEventTypeName: IssueEventTypeName) : Template? {
        return templateRepository.findByKey(issueEventTypeName.name.toLowerCase())?.let{
            Template("issue_template", StringReader(it.template), cfg)
        }
    }
}