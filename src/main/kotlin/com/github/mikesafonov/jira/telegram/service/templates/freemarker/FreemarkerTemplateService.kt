package com.github.mikesafonov.jira.telegram.service.templates.freemarker

import com.github.mikesafonov.jira.telegram.dao.TemplateRepository
import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.dto.IssueEventTypeName
import com.github.mikesafonov.jira.telegram.service.templates.CompiledTemplate
import com.github.mikesafonov.jira.telegram.service.templates.TemplateService
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import freemarker.template.Version
import mu.KotlinLogging
import no.api.freemarker.java8.Java8ObjectWrapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.io.StringReader
import java.io.StringWriter

/**
 * This class wraps logic for compile and building [Freemarker](https://freemarker.apache.org) templates
 *
 * @author Mike Safonov
 */
private val logger = KotlinLogging.logger {}

@Service
@ConditionalOnProperty(prefix = "jira.bot.template", name = arrayOf("type"), havingValue = "FREEMARKER")
class FreemarkerTemplateService(private val templateRepository: TemplateRepository) : TemplateService {

    private val version = Version(2, 3, 20)
    private val cfg = Configuration(version)

    init {
        cfg.defaultEncoding = "UTF-8"
        cfg.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
        cfg.objectWrapper = Java8ObjectWrapper(version)
    }

    /**
     * Builds telegram message for issue type [event] and freemarker template from database
     * @param event jira event
     * @param parameters input parameters for template
     * @return builded *markdown* message or null if template for event [event] not exist
     */
    override fun buildMessage(event: Event, parameters: Map<String, Any>): CompiledTemplate? {
        val issueEventTypeName = event.issueEventTypeName
        if (issueEventTypeName != null) {
            val eventTemplate = getByIssueType(issueEventTypeName)
            if (eventTemplate != null) {
                val sw = StringWriter()
                eventTemplate.process(parameters, sw)
                return CompiledTemplate(sw.toString(), true)
            }

            logger.debug { "template for type $issueEventTypeName not found" }
        }
        return null
    }

    /**
     * Finds template in database
     * @param issueEventTypeName type of issue
     * @return freemarker [Template]
     */
    private fun getByIssueType(issueEventTypeName: IssueEventTypeName): Template? {
        val templateKey = issueEventTypeName.name.toLowerCase()
        return templateRepository.findByKey(templateKey)?.let {
            Template(templateKey, StringReader(it.template), cfg)
        }
    }


}