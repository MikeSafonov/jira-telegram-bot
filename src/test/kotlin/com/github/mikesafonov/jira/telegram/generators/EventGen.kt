package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.*
import io.kotlintest.properties.Gen


class EventGen : Gen<Event> {

    companion object {
        fun generateDefault(): Event {
            return EventGen().generateOne()
        }

        fun empty(): Event? {
            return null
        }
    }


    override fun constants(): Iterable<Event> {
        return emptyList()
    }

    override fun random(): Sequence<Event> {
        return generateSequence { generateOne() }
    }

    fun generateOne(
        webHookEvent: WebHookEvent = Gen.enum<WebHookEvent>().random().first(),
        issueEventTypeName: IssueEventTypeName? = Gen.enum<IssueEventTypeName>().random().first(),
        timestamp: Long = Gen.long().random().first(),
        user: User? = UserGen.generateDefault(),
        issue: Issue? = IssueGen.generateDefault(),
        comment: Comment? = CommentGen.generateDefault(),
        changelog: Changelog? = ChangelogGen.generateDefault()
    ): Event {
        return Event(webHookEvent, issueEventTypeName, timestamp, user, issue, comment, changelog)
    }

}