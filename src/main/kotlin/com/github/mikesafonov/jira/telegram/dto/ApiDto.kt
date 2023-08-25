package com.github.mikesafonov.jira.telegram.dto

/**
 * @author Mike Safonov
 */
data class SendToAll(val message: String)

data class FilterSubscription(val idFilter: Long, val jiraLogin: String)