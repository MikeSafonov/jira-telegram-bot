package com.github.mikesafonov.jira.telegram

import java.time.LocalDateTime

data class Event(val event: String, val timestamp: String, val user: User?,
                 val issue: Issue?, val comment: Comment?, val changelog: Changelog?)
data class Issue(val id:Long, val self: String, val key:String)
data class Comment(val body: String, val created: LocalDateTime?, val author:User?,
                   val updateAuthor: User?, val updated: LocalDateTime?)
data class Changelog(val fromString: String, val toString: String?)
data class User(val name:String)
