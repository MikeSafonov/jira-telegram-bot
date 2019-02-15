package com.github.mikesafonov.jira.telegram.dto

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.mikesafonov.jira.telegram.config.JiraLocalDateTimeDeserializer
import java.time.LocalDateTime


enum class WebHookEvent {
    @JsonProperty("comment_created")
    COMMENT_CREATED,
    @JsonProperty("comment_updated")
    COMMENT_UPDATED,
    @JsonProperty("comment_deleted")
    COMMENT_DELETED,

    @JsonProperty("jira:issue_updated")
    JIRA_ISSUE_UPDATED,
    @JsonProperty("jira:issue_created")
    JIRA_ISSUE_CREATED,
    @JsonProperty("jira:issue_deleted")
    JIRA_ISSUE_DELETED

}

enum class IssueEventTypeName {
    @JsonProperty("issue_commented")
    ISSUE_COMMENTED,
    @JsonProperty("issue_created")
    ISSUE_CREATED,
    @JsonProperty("issue_generic")
    ISSUE_GENERIC,
    @JsonProperty("issue_updated")
    ISSUE_UPDATED,
    @JsonProperty("issue_comment_edited")
    ISSUE_COMMENT_EDITED,
    @JsonProperty("issue_comment_deleted")
    ISSUE_COMMENT_DELETED,
}

data class Event(
    val webhookEvent: WebHookEvent,
    @JsonAlias("issue_event_type_name")
    val issueEventTypeName: IssueEventTypeName?,
    val timestamp: Long,
    val user: User?,
    val issue: Issue?,
    val comment: Comment?,
    val changelog: Changelog?
) {
    fun isIssueEvent(): Boolean {
        return webhookEvent == WebHookEvent.JIRA_ISSUE_UPDATED || webhookEvent == WebHookEvent.JIRA_ISSUE_CREATED
                || webhookEvent == WebHookEvent.JIRA_ISSUE_DELETED
    }
}


data class Changelog(
    val id: String,
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