package com.github.mikesafonov.jira.telegram.service.destination

import com.github.mikesafonov.jira.telegram.dto.Event

/**
 * Interface for detecting list of logins to send jira event notification
 * @author Mike Safonov
 */
interface DestinationDetectorService {

    /**
     * collect jira logins from [event] for notification
     */
    fun findDestinations(event: Event): List<String>
}