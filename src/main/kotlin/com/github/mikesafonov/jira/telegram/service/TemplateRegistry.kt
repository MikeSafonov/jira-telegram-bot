package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.dao.TemplateRepository
import com.github.mikesafonov.jira.telegram.dto.IssueEventTypeName
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import freemarker.template.Version
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.io.StringReader


private val logger = KotlinLogging.logger {}

@Service
class TemplateRegistry(val templateRepository: TemplateRepository) {

    private val cfg = Configuration(Version(2, 3, 20))

    init {
        cfg.defaultEncoding = "UTF-8"
        cfg.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
    }

    fun getByIssueType(issueEventTypeName: IssueEventTypeName?) : Template? {
        return issueEventTypeName?.let {
            return templateRepository.findByKey(it.name.toLowerCase())?.let{
                Template("issue_template", StringReader(it.template), cfg)
            }
        }
    }
}