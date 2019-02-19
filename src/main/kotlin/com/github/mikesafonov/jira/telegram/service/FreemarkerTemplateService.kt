package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.dao.TemplateRepository
import com.github.mikesafonov.jira.telegram.dto.IssueEventTypeName
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import freemarker.template.Version
import no.api.freemarker.java8.Java8ObjectWrapper
import org.springframework.stereotype.Service
import java.io.StringReader
import java.io.StringWriter

/**
 * This class wraps logic for compile and building [Freemarker](https://freemarker.apache.org) templates
 *
 * @author Mike Safonov
 */
@Service
class FreemarkerTemplateService(private val templateRepository: TemplateRepository) {

    private val version = Version(2, 3, 20)
    private val cfg = Configuration(version)

    init {
        cfg.defaultEncoding = "UTF-8"
        cfg.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
        cfg.objectWrapper = Java8ObjectWrapper(version)
    }

    /**
     * Builds telegram message for issue type [issueEventTypeName] and freemarker template from database
     * @param issueEventTypeName type of issue
     * @param parameters input parameters for template
     * @return builded message or null if template for type [issueEventTypeName] not exist
     */
    fun buildMessage(issueEventTypeName: IssueEventTypeName, parameters: Map<String, Any>): String? {
        return getByIssueType(issueEventTypeName)?.let {
            val sw = StringWriter()
            it.process(parameters, sw)
            return sw.toString()
        }
    }

    /**
     * Finds template in database
     * @param issueEventTypeName type of issue
     * @return freemarker [Template]
     */
    private fun getByIssueType(issueEventTypeName: IssueEventTypeName): Template? {
        return templateRepository.findByKey(issueEventTypeName.name.toLowerCase())?.let {
            Template("issue_template", StringReader(it.template), cfg)
        }
    }


}