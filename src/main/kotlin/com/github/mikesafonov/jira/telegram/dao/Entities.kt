package com.github.mikesafonov.jira.telegram.dao

import java.io.Serializable
import javax.persistence.*

/**
 * @author Mike Safonov
 */
@Entity
@Table(name = "chats")
data class Chat(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int?,

    @Column(name = "jira_id", nullable = false)
    val jiraId: String,

    @Column(name = "telegram_id", nullable = false)
    val telegramId: Long,

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    var state: State,

    @ManyToMany
    @JoinTable(
        name = "chats_tags",
        joinColumns = [JoinColumn(name = "id_chat", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "id_tag", referencedColumnName = "id")]
    )
    var tags: List<Tag> = mutableListOf(),
)

/**
 * @author Mike Safonov
 */
@Entity
@Table(name = "templates")
data class Template(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(name = "template_key", nullable = false)
    val key: String,

    @Column(name = "template", nullable = false)
    val template: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "parse_mode", nullable = false)
    val parseMode: TemplateParseMode
)

enum class TemplateParseMode {
    MARKDOWN,
    MARKDOWN_V2,
    HTML
}

@Entity
@Table(name = "authorizations")
data class Authorization(
    @Id
    val id: Long?,

    @Column(name = "access_token")
    var accessToken: String?,

    @Column(name = "secret_token")
    var secretToken: String?
)

enum class State {
    INIT,
    WAIT_APPROVE
}

@Entity
@Table(name = "tags")
data class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @Column(name = "tag", nullable = false)
    val key: String,

    @ManyToMany(mappedBy = "tags")
    var chats: List<Chat> = mutableListOf()
)

@Entity
@Table(name = "chats_tags")
data class ChatTag(
    @EmbeddedId
    val id: ChatTagId
)

@Embeddable
data class ChatTagId (
    @Column(name = "id_tag")
    val idTag: Long?,
    @Column(name = "id_chat")
    val idChat: Int?
) : Serializable
