package com.github.mikesafonov.jira.telegram

import com.github.mikesafonov.jira.telegram.dto.WebHookEvent
import io.kotlintest.properties.Gen

/**
 * @author Mike Safonov
 */

class IssueEventTypesGen : Gen<WebHookEvent> {
    override fun constants(): Iterable<WebHookEvent> {
        return listOf(
            WebHookEvent.JIRA_ISSUE_CREATED,
            WebHookEvent.JIRA_ISSUE_DELETED,
            WebHookEvent.JIRA_ISSUE_UPDATED
        )
    }

    override fun random(): Sequence<WebHookEvent> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}