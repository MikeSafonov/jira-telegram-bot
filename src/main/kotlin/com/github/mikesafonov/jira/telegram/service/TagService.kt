package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.dao.Tag
import com.github.mikesafonov.jira.telegram.dao.TagRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author Mike Safonov
 */
@Service
class TagService(private val repository: TagRepository) {

    fun getTagByKey(key: String): Tag? {
        return repository.findByKey(key)
    }

    @Transactional
    fun getJiraLoginsByTagKey(key: String): List<String> {
        val tag = getTagByKey(key) ?: return emptyList()
        return tag.chats.map { it.jiraId }
    }
}
