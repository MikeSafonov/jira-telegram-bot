package com.github.mikesafonov.jira.telegram.dto

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.mikesafonov.jira.telegram.config.JiraLocalDateTimeDeserializer
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


/**
 * Type of jira event
 * @author Mike Safonov
 */
enum class WebHookEvent {
    /**
     * fired if comment created
     */
    @JsonProperty("comment_created")
    COMMENT_CREATED,
    /**
     * fired if comment updated
     */
    @JsonProperty("comment_updated")
    COMMENT_UPDATED,
    /**
     * fired if comment deleted
     */
    @JsonProperty("comment_deleted")
    COMMENT_DELETED,

    /**
     * fired if issue updated
     */
    @JsonProperty("jira:issue_updated")
    JIRA_ISSUE_UPDATED,
    /**
     * fired if issue created
     */
    @JsonProperty("jira:issue_created")
    JIRA_ISSUE_CREATED,
    /**
     * fired if issue deleted
     */
    @JsonProperty("jira:issue_deleted")
    JIRA_ISSUE_DELETED;

    companion object {
        fun issueEvents(): Array<WebHookEvent> {
            return arrayOf(
                WebHookEvent.JIRA_ISSUE_UPDATED,
                WebHookEvent.JIRA_ISSUE_CREATED,
                WebHookEvent.JIRA_ISSUE_DELETED
            )
        }
    }

    fun isIssueEvent(): Boolean {
        return this in issueEvents()
    }

}

/**
 * Type of jira issue event
 * @author Mike Safonov
 */
enum class IssueEventTypeName {
    /**
     * fired if issue commented
     */
    @JsonProperty("issue_commented")
    ISSUE_COMMENTED,
    /**
     * fired if issue created
     */
    @JsonProperty("issue_created")
    ISSUE_CREATED,
    /**
     * fired if issue moved through workflow
     */
    @JsonProperty("issue_generic")
    ISSUE_GENERIC,
    /**
     * fired if issue fields updated
     */
    @JsonProperty("issue_updated")
    ISSUE_UPDATED,
    /**
     * fired if issue comment edited
     */
    @JsonProperty("issue_comment_edited")
    ISSUE_COMMENT_EDITED,
    /**
     * fired if issue comment deleted
     */
    @JsonProperty("issue_comment_deleted")
    ISSUE_COMMENT_DELETED,

    @JsonProperty("issue_assigned")
    ISSUE_ASSIGNED
}

/**
 * Jira event
 * @author Mike Safonov
 */
data class Event(
    /**
     * type of jira event
     */
    val webhookEvent: WebHookEvent,
    /**
     * type of jira issue event
     */
    @JsonAlias("issue_event_type_name")
    val issueEventTypeName: IssueEventTypeName?,
    /**
     * evet timestamp in long
     */
    val timestamp: Long,
    /**
     * jira user
     */
    val user: User?,
    /**
     * jira issue
     */
    val issue: Issue?,
    /**
     * jira comment
     */
    val comment: Comment?,
    /**
     * jira chengelog
     */
    val changelog: Changelog?
) {
    /**
     * jira event timestamp in [LocalDateTime]
     */
    val eventDate: LocalDateTime

    init {
        eventDate = timestampAsDate()
    }

    /**
     * name of project
     */
    val projectName: String
        get() {
            return issue?.fields?.project?.name ?: ""
        }

    /**
     * is [webhookEvent] have one of [WebHookEvent.JIRA_ISSUE_UPDATED], [WebHookEvent.JIRA_ISSUE_CREATED] or [WebHookEvent.JIRA_ISSUE_DELETED] values
     */
    val isIssueEvent: Boolean
        get() {
            return webhookEvent.isIssueEvent()
        }

    /**
     * convert long timestamp representation to [LocalDateTime]
     * @return jira event timestamp in [LocalDateTime]
     */
    private fun timestampAsDate(): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    }


}

/**
 * Jira changelog data class
 * @author Mike Safonov
 */
data class Changelog(
    /**
     * changelog id
     */
    val id: String,
    /**
     * list of changed elements
     */
    val items: Array<ChangelogItem> = emptyArray()
)

/**
 * Element of jra chengelog
 * @author Mike Safonov
 */
data class ChangelogItem(
    /**
     * name of changed field
     */
    val field: String,
    /**
     * initial value of [field]
     */
    val fromString: String?,
    /**
     * new value of [field]
     */
    @JsonAlias("toString")
    val newString: String?
) {

    /**
     * is [fromString] and [newString] exist`s
     */
    val changed: Boolean
        get() {
            return fromString != null && newString != null
        }

    /**
     * is [fromString] == null and [newString] exist
     */
    val added: Boolean
        get() {
            return fromString == null && newString != null
        }

    /**
     * is [fromString] exist and [newString] == null
     */
    val removed: Boolean
        get() {
            return fromString != null && newString == null
        }

    /**
     * is [field] equals to "status"
     */
    val statusChanged: Boolean
        get() {
            return "status" == this.field
        }

    /**
     * is [field] equals to "assignee"
     */
    val assigneeChanged: Boolean
        get() {
            return "assignee" == this.field
        }
}


/**
 * Jira user
 * @author Mike Safonov
 */
data class User(
    /**
     * user`s login
     */
    val name: String,
    /**
     * user`s display name
     */
    val displayName: String
)


/**
 * Jira comment data class
 * @author Mike Safonov
 */
data class Comment(
    /**
     * comment`s body
     */
    val body: String,
    /**
     * comment`s author
     */
    val author: User?,
    /**
     * comment`s creation date
     */
    @field:JsonDeserialize(using = JiraLocalDateTimeDeserializer::class)
    val created: LocalDateTime,
    /**
     * comment`s update author
     */
    val updateAuthor: User?,
    /**
     * comment`s link of rest api
     */
    val self: String,
    /**
     * comment`s update date
     */
    @field:JsonDeserialize(using = JiraLocalDateTimeDeserializer::class)
    val updated: LocalDateTime?
)


/**
 * Jira project data class
 *
 * @author Mike Safonov
 */
data class Project(
    /**
     * jira project id
     */
    val id: Long,
    /**
     * jira project link of rest api
     */
    val self: String,
    /**
     * jira project key
     */
    val key: String,
    /**
     * jira project name
     */
    val name: String
)

/**
 * Jira component
 * @author Mike Safonov
 */
data class JiraComponent(
    /**
     * component rest api link
     */
    val self: String,
    /**
     * component name
     */
    val name: String
)

/**
 * Jira attachment
 * @author Mike Safonov
 */
data class Attachment(
    /**
     * attachment name
     */
    val filename: String,
    /**
     * link to jira attachment
     */
    val content: String
)

/**
 * Jira version
 * @author Mike Safonov
 */
data class Version(
    /**
     * version id
     */
    val id: Long,
    /**
     * version rest api link
     */
    val self: String,
    /**
     * version description
     */
    val description: String,
    /**
     * version name
     */
    val name: String,
    /**
     * is version archived
     */
    val archived: Boolean,
    /**
     * is version released
     */
    val released: Boolean
)

/**
 * Issue status
 * @author Mike Safonov
 */
data class Status(
    /**
     * status id
     */
    val id: String,
    /**
     * status description
     */
    val description: String,
    /**
     * status name
     */
    val name: String
)

/**
 * Issue priority
 * @author Mike Safonov
 */
data class Priority(
    /**
     * name of priority
     */
    val name: String,
    /**
     * url of priority icon
     */
    val iconUrl: String
)