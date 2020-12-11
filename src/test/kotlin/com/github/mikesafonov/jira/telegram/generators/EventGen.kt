package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.*
import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next


class EventGen {

    companion object {
        fun generateDefault(): Event {
            return EventGen().generateOne()
        }

        fun empty(): Event? {
            return null
        }
    }


    fun constants(): Iterable<Event> {
        return emptyList()
    }

    fun random(): Sequence<Event> {
        return generateSequence { generateOne() }
    }

    fun generateOne(
        webHookEvent: WebHookEvent = Arb.enum<WebHookEvent>().next(),
        issueEventTypeName: IssueEventTypeName? = Arb.enum<IssueEventTypeName>().next(),
        timestamp: Long = Arb.long().next(),
        user: User? = UserGen.generateDefault(),
        issue: Issue? = IssueGen.generateDefault(),
        comment: Comment? = CommentGen.generateDefault(),
        changelog: Changelog? = ChangelogGen.generateDefault()
    ): Event {
        return Event(webHookEvent, issueEventTypeName, timestamp, user, issue, comment, changelog)
    }

    fun random(seed: Long?): Sequence<Event> {
        return random()
    }

}
