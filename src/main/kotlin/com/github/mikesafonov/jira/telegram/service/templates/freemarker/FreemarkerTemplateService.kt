package com.github.mikesafonov.jira.telegram.service.templates.freemarker

import com.github.mikesafonov.jira.telegram.service.templates.CompiledTemplate
import com.github.mikesafonov.jira.telegram.service.templates.RawTemplate
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
class FreemarkerTemplateService : TemplateService {

    private val version = Version(2, 3, 20)
    private val cfg = Configuration(version)

    init {
        cfg.defaultEncoding = "UTF-8"
        cfg.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
        cfg.objectWrapper = Java8ObjectWrapper(version)
    }

    /**
     * Builds telegram message for raw freemarker template [rawTemplate]
     * @param rawTemplate raw freemarker template
     * @return builded *markdown* message
     */
    override fun buildMessage(rawTemplate: RawTemplate): CompiledTemplate {
        val template = Template(rawTemplate.templateKey, StringReader(rawTemplate.template), cfg)
        val sw = StringWriter()
        template.process(rawTemplate.parameters, sw)
        return CompiledTemplate(sw.toString(), true)
    }
}