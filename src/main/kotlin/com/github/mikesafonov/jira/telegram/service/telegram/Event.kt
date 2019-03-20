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

    companion object {
        fun parse(value : String) : Event {
            return when(value){
                "/help" -> HELP_REQUEST
                "/me" -> ME_REQUEST
                "/jira_login" -> LOGIN_REQUEST
                "/users_list" -> USERS_REQUEST
                "/add_user" -> ADD_USER_REQUEST
                "/remove_user" -> REMOVE_USER_REQUEST
                "/auth" -> AUTH_REQUEST
                else -> UNKNOWN
            }
        }
    }

}