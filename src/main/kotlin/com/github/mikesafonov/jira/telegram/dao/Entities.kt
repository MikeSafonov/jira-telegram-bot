package com.github.mikesafonov.jira.telegram.dao

import javax.persistence.*

@Entity
@Table(name = "chats")
data class Chat(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(name = "jira_id", nullable = false)
    val jiraId: String,

    @Column(name = "telegram_id", nullable = false)
    val telegramId: Long
)

@Entity
@Table(name = "templates")
data class Template(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(name = "template_key", nullable = false)
    val key: String,

    @Column(name = "template", nullable = false)
    val template: String
)