package com.github.mikesafonov.jira.telegram.service.templates.freemarker

import com.github.mikesafonov.jira.telegram.service.templates.CompiledTemplate
import com.github.mikesafonov.jira.telegram.service.templates.RawTemplate
import com.github.mikesafonov.jira.telegram.service.templates.TemplateService
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
     * @return built *markdown* message
     */
    override fun buildMessage(rawTemplate: RawTemplate): CompiledTemplate {
        val template = Template(rawTemplate.templateKey, StringReader(rawTemplate.template), cfg)
        val sw = StringWriter()
        val parameters = rawTemplate.parameters
        parameters["mode"] = rawTemplate.parseMode
        template.process(parameters, sw)
        return CompiledTemplate(sw.toString(), rawTemplate.parseMode)
    }
}
