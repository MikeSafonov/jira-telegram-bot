package com.github.mikesafonov.jira.telegram.service.telegram.handlers

/**
 * @author Mike Safonov
 */
interface ArgParser {
    /**
     * Method collects command arguments from full command text [message]
     * @param message text command
     * @return list of command arguments
     */
    fun getCommandArgs(message: String): List<String> {
        return message.split(" ")
    }
}
