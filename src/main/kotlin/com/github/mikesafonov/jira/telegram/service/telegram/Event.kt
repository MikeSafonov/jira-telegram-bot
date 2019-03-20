package com.github.mikesafonov.jira.telegram.service.telegram

/**
 * @author Mike Safonov
 */
enum class Event {
    UNKNOWN,
    HELP_REQUEST,
    ME_REQUEST,
    LOGIN_REQUEST,
    USERS_REQUEST,
    ADD_USER_REQUEST,
    REMOVE_USER_REQUEST,
    AUTH_REQUEST;
}