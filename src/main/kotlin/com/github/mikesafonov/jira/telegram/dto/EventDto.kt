package com.github.mikesafonov.jira.telegram.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.mikesafonov.jira.telegram.config.JiraLocalDateTimeDeserializer
import java.time.LocalDateTime


data class Event(
    val webhookEvent: String,
    val timestamp: Long,
    val user: User?,
    val issue_event_type_name: String,
    val issue: Issue?,
    val comment: Comment?,
    val changelog: Changelog?
)


data class Changelog(
    val id:String,
    val items: Array<ChangelogItem> = emptyArray()
)

data class ChangelogItem(
    val field: String,
    val fromString: String?,
    val toString: String?
)


data class User(
    val name: String,
    val displayName: String
)


data class Comment(
    val body: String,
    val author: User?,
    @field:JsonDeserialize(using = JiraLocalDateTimeDeserializer::class)
    val created: LocalDateTime,
    val updateAuthor: User?,
    val self: String,
    @field:JsonDeserialize(using = JiraLocalDateTimeDeserializer::class)
    val updated: LocalDateTime?
)


data class Project(
    val id: Long,
    val self: String,
    val key: String,
    val name: String
)