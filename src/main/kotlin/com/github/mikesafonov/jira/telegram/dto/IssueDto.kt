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

    val versionsAsString: String
        get() {
            return fields.fixVersions.joinToString { it.name }
        }

    val labelsAsString: String
        get() {
            return fields.labels.joinToString()
        }

    val componentsAsString: String
        get() {
            return fields.components.joinToString { it.name }
        }

    val creatorName: String
        get() {
            return fields.creator.name
        }

    val assigneeName: String?
        get() {
            return fields.assignee?.name
        }

    val reporterName: String?
        get() {
            return fields.reporter?.name
        }

    val creatorDisplayName: String
        get() {
            return fields.creator.displayName
        }

    val assigneeDisplayName: String?
        get() {
            return fields.assignee?.displayName
        }

    val reporterDisplayName: String?
        get() {
            return fields.reporter?.displayName
        }

    val containsVersions: Boolean
        get() {
            return !fields.fixVersions.isEmpty()
        }

    val containsLabels: Boolean
        get() {
            return !fields.labels.isEmpty()
        }

    val containsAttachments: Boolean
        get() {
            return !fields.attachment.isEmpty()
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

data class Attachment(
    val filename: String,
    val content: String
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

data class Priority(val name: String, val iconUrl: String)









