package com.github.mikesafonov.jira.telegram.dao

import org.springframework.data.jpa.repository.JpaRepository

interface ChatRepository : JpaRepository<Chat, Int> {
    fun findByJiraId(jiraId: String): Chat?
}

interface TemplateRepository : JpaRepository<Template, Int> {
    fun findByKey(key: String): Template?
}