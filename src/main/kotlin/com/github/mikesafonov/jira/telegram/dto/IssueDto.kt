package com.github.mikesafonov.jira.telegram.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.mikesafonov.jira.telegram.config.JiraLocalDateTimeDeserializer
import java.time.LocalDateTime


data class Issue(
    val id: Long,
    val self: String,
    val key: String,
    val fields: IssueFields
) {
    fun creatorName(): String {
        return fields.creator.name
    }

    fun assigneeName(): String? {
        return fields.assignee?.name
    }

    fun reporterName(): String? {
        return fields.reporter?.name
    }

    fun creatorDisplayName(): String {
        return fields.creator.displayName
    }

    fun assigneeDisplayName(): String? {
        return fields.assignee?.displayName
    }

    fun reporterDisplayName(): String? {
        return fields.reporter?.displayName
    }

    fun containsVersions(): Boolean {
        return !fields.fixVersions.isEmpty()
    }

    fun containsLabels(): Boolean {
        return !fields.labels.isEmpty()
    }

    fun containsAttachments(): Boolean {
        return !fields.attachment.isEmpty()
    }

    fun versionsToString(): String {
        return fields.fixVersions.map { it.name }.joinToString()
    }

    fun labelsToString(): String {
        return fields.labels.joinToString()
    }

    fun componentsToString() : String {
        return fields.components.map{it.name}.joinToString()
    }
}

data class IssueFields(
    val summary: String,
    val description: String,
    val project: Project?,
    val creator: User,
    val issuetype: IssueType,
    val fixVersions: Array<Version> = emptyArray(),
    val attachment: Array<Attachment> = emptyArray(),
    @field:JsonDeserialize(using = JiraLocalDateTimeDeserializer::class)
    val created: LocalDateTime,
    val reporter: User?,
    val assignee: User?,
    @field:JsonDeserialize(using = JiraLocalDateTimeDeserializer::class)
    val updated: LocalDateTime?,
    val status: Status,
    val priority: Priority,
    val components: Array<JiraComponent> = emptyArray(),
    val labels: Array<String> = emptyArray()
)

data class IssueType(
    val name: String,
    val description: String
)

data class JiraComponent(
    val self: String,
    val name: String
)

data class Attachment (
    val filename : String,
    val content : String
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

data class Priority(val name: String, val iconUrl : String)









