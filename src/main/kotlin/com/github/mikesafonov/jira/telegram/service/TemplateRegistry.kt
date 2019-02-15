package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.dto.IssueEventTypeName
import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.File
import java.io.StringReader
import javax.annotation.PostConstruct

private val logger = KotlinLogging.logger {}

@Service
class TemplateRegistry {

    @Value("\${jira.event.template.path}")
    var templatePath: String? = null

    private val issueTemplates: MutableMap<IssueEventTypeName, Mustache> = HashMap()

    @PostConstruct
    fun postConstruct() {

        logger.info("Initializing templates")

        if (templatePath.isNullOrBlank()) {
            logger.info { "Reading default templates" }
            loadDefaultTemplates()
        } else {
            logger.info { "Reading templates from $templatePath" }
            val folder = File(templatePath)
            if (folder.isDirectory) {
                IssueEventTypeName.values().forEach {
                    val name = it.name.toLowerCase()
                    val templateFile = folder.listFiles().find { file -> file.name == name }
                    if (templateFile != null) {
                        val template = templateFile.readText()
                        val mustache = readTemplateFromString(template)
                        issueTemplates[it] = mustache
                    } else {
                        throw RuntimeException("Unable to find template for type $it")
                    }
                }
            } else {
                throw RuntimeException("$templatePath is not folder")
            }
        }
    }

    private fun loadDefaultTemplates() {
        IssueEventTypeName.values().forEach {
            val mustache = getDefaultTemplateByType(it)
            issueTemplates[it] = mustache
        }
    }

    private fun getDefaultTemplateByType(issueEventTypeName: IssueEventTypeName): Mustache {
        val name = issueEventTypeName.name.toLowerCase()
        val defaultTemplate = ClassPathResource("templates/$name.mustache").file.readText()
        return readTemplateFromString(defaultTemplate)
    }

    private fun readTemplateFromString(template: String): Mustache {
        val mf = DefaultMustacheFactory()
        return mf.compile(StringReader(template), "template")
    }


    fun getByIssueType(issueEventTypeName: IssueEventTypeName?): Mustache? {
        if (issueEventTypeName != null) {
            return issueTemplates.get(issueEventTypeName)
        }
        return null
    }
}