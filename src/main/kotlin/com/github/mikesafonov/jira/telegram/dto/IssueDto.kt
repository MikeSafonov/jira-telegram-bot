package com.github.mikesafonov.jira.telegram.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.mikesafonov.jira.telegram.config.JiraLocalDateTimeDeserializer
import java.time.LocalDateTime


data class Issue(
    val id: Long,
    val self: String,
    val key: String,
    val fields: IssueFields
)

data class IssueFields(
    val summary: String,
    val description: String,
    val project: Project?,
    val creator: User,
    val issuetype: IssueType,
    val fixVersions: Array<Version> = emptyArray(),
    @field:JsonDeserialize(using = JiraLocalDateTimeDeserializer::class)
    val created: LocalDateTime,
    val reporter: User?,
    val assignee: User?,
    @field:JsonDeserialize(using = JiraLocalDateTimeDeserializer::class)
    val updated: LocalDateTime?,
    val status: Status,
    val priority: Priority,
    val components: Array<JiraComponent> = emptyArray(),
    val labels : Array<String> = emptyArray()
)

data class IssueType(
    val name: String,
    val description: String
)

data class JiraComponent(
    val self: String,
    val name: String
)

data class Version(
    val id: Long,
    val self: String,
    val description: String,
    val name: String,
    val archived: Boolean,
    val released: Boolean
)

data class Status(
    val id: String,
    val description: String,
    val name: String
)

data class Priority(val name: String)









