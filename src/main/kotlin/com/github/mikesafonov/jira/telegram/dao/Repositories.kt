package com.github.mikesafonov.jira.telegram.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

/**
 * @author Mike Safonov
 */
interface ChatRepository : JpaRepository<Chat, Int> {
    fun findByJiraId(jiraId: String): Chat?

    fun findByTelegramId(telegramId: Long): Chat?
}

/**
 * @author Mike Safonov
 */
interface TemplateRepository : JpaRepository<Template, Int> {
    fun findByKey(key: String): Template?
}

interface AuthorizationRepository : JpaRepository<Authorization, Long> {

}

interface TagRepository : JpaRepository<Tag, Long> {
    fun findByKey(key: String): Tag?
}


interface ChatTagRepository : JpaRepository<ChatTag, ChatTagId> {
    @Modifying
    @Query("delete from ChatTag ct where ct.id.idChat = ?1")
    fun deleteByIdChat( chatId: Int)
}
