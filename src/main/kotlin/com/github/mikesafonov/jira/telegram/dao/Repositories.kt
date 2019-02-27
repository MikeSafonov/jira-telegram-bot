package com.github.mikesafonov.jira.telegram.dao

import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author Mike Safonov
 */
interface ChatRepository : JpaRepository<Chat, Int> {
    fun findByJiraId(jiraId: String): Chat?

    fun findByTelegramId(telegramId: Long): Chat?

    fun deleteByJiraId(jiraId: String)
}

/**
 * @author Mike Safonov
 */
interface TemplateRepository : JpaRepository<Template, Int> {
    fun findByKey(key: String): Template?
}