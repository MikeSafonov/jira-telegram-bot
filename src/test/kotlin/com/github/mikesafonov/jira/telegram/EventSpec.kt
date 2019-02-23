package com.github.mikesafonov.jira.telegram

import com.github.mikesafonov.jira.telegram.dto.WebHookEvent
import com.github.mikesafonov.jira.telegram.generators.EventGen
import io.kotlintest.matchers.string.shouldHaveLength
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class EventSpec : StringSpec({
    "Event should contain project name if issue exist"{
        val event = EventGen.generateDefault()
        event.projectName shouldBe event.issue?.fields?.project?.name
    }

    "Event should not contain project name"{
        val event = EventGen().generateOne(issue = null)
        event.projectName shouldHaveLength 0
    }

    "Event should issue event if WebHookEvent in list"{
        val issueEventTypes = arrayOf(
            WebHookEvent.JIRA_ISSUE_UPDATED,
            WebHookEvent.JIRA_ISSUE_CREATED,
            WebHookEvent.JIRA_ISSUE_DELETED
        )

        WebHookEvent.values().forEach {
            val isIssueEventExpected = it in issueEventTypes
            val event = EventGen().generateOne(webHookEvent = it)
            event.isIssueEvent shouldBe isIssueEventExpected
        }
    }
})
