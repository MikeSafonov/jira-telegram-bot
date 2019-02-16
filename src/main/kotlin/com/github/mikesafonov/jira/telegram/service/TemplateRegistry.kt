package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.dao.TemplateRepository
import com.github.mikesafonov.jira.telegram.dto.IssueEventTypeName
import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.io.StringReader

private val logger = KotlinLogging.logger {}

@Service
class TemplateRegistry(val templateRepository: TemplateRepository) {

    fun getByIssueType(issueEventTypeName: IssueEventTypeName?): Mustache? {
        return issueEventTypeName?.let {
            return findAndCompileTemplate(it.name.toLowerCase())
        }
    }

    private fun findAndCompileTemplate(key: String): Mustache? {
        return templateRepository.findByKey(key)?.let {
            val mf = DefaultMustacheFactory()
            return mf.compile(StringReader(it.template), "template")
        }
    }
}