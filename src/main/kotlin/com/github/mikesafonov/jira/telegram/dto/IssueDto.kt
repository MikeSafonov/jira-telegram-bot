package com.github.mikesafonov.jira.telegram.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.mikesafonov.jira.telegram.config.JiraLocalDateTimeDeserializer
import java.time.LocalDateTime

/**
 * Jira issue
 * @author Mike Safonov
 */
data class Issue(
    /**
     * issue id
     */
    val id: Long,
    /**
     * issue link to rest api
     */
    val self: String,
    /**
     * issue key
     */
    val key: String,
    /**
     * issue fields
     */
    val fields: IssueFields
) {

    /**
     * String of [IssueFields.fixVersions] concatenated by ','
     */
    val versionsAsString: String
        get() {
            return fields.fixVersions.joinToString { it.name }
        }

    /**
     * String of [IssueFields.labels] concatenated by ','
     */
    val labelsAsString: String
        get() {
            return fields.labels.joinToString()
        }

    /**
     * String of [IssueFields.components] concatenated by ','
     */
    val componentsAsString: String
        get() {
            return fields.components.joinToString { it.name }
        }

    /**
     * name of issue creator
     */
    val creatorName: String
        get() {
            return fields.creator.name
        }

    /**
     * name of issue assignee
     */
    val assigneeName: String?
        get() {
            return fields.assignee?.name
        }

    /**
     * name of issue reporter
     */
    val reporterName: String?
        get() {
            return fields.reporter?.name
        }

    /**
     * issue creator display name
     */
    val creatorDisplayName: String
        get() {
            return fields.creator.displayName
        }

    /**
     * issue assignee display name
     */
    val assigneeDisplayName: String?
        get() {
            return fields.assignee?.displayName
        }

    /**
     * issue reporter display name
     */
    val reporterDisplayName: String?
        get() {
            return fields.reporter?.displayName
        }

    /**
     * is this issue contains linked versions
     */
    val containsVersions: Boolean
        get() {
            return !fields.fixVersions.isEmpty()
        }

    /**
     * is this issue contains linked labels
     */
    val containsLabels: Boolean
        get() {
            return !fields.labels.isEmpty()
        }

    /**
     * is this issue contains linked attachments
     */
    val containsAttachments: Boolean
        get() {
            return !fields.attachment.isEmpty()
        }

}

/**
 * Issue fields
 * @author Mike Safonov
 */
data class IssueFields(
    /**
     * issue summary
     */
    val summary: String,
    /**
     * issue description
     */
    val description: String?,
    /**
     * issue project
     */
    val project: Project?,
    /**
     * user who create this issue
     */
    val creator: User,
    /**
     * issue type
     */
    val issuetype: IssueType,
    /**
     * fixVersions to which issue belongs
     */
    val fixVersions: Array<Version> = emptyArray(),
    /**
     * issue attachments
     */
    val attachment: Array<Attachment> = emptyArray(),
    @field:JsonDeserialize(using = JiraLocalDateTimeDeserializer::class)
    val created: LocalDateTime,
    /**
     * user reporter of this issue
     */
    val reporter: User?,
    /**
     * user assignee of this issue
     */
    val assignee: User?,
    /**
     * update date
     */
    @field:JsonDeserialize(using = JiraLocalDateTimeDeserializer::class)
    val updated: LocalDateTime?,
    /**
     * issue status
     */
    val status: Status,
    /**
     * issue priority
     */
    val priority: Priority,
    /**
     * components to which issue belongs
     */
    val components: Array<JiraComponent> = emptyArray(),
    /**
     * label to which issue belongs
     */
    val labels: Array<String> = emptyArray(),

    /**
     * watchers
     */
    val watches: Watchers?
)

/**
 * Issue type
 * @author Mike Safonov
 */
data class IssueType(
    /**
     * type name
     */
    val name: String,
    /**
     * type description
     */
    val description: String
)

data class Watchers(
    /**
     * watchers link to rest api
     */
    val self: String,

    val watchCount: Int,

    val isWatching: Boolean
)
