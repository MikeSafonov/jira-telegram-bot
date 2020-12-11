package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.*
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import java.time.LocalDateTime

/**
 * @author Mike Safonov
 */

class IssueFieldsGen {

    companion object {
        fun generateDefault(): IssueFields {
            return IssueFieldsGen().generateOne()
        }

        fun empty(): IssueFields? {
            return null
        }
    }


    fun constants(): Iterable<IssueFields> {
        return emptyList()
    }

    fun random(): Sequence<IssueFields> {
        return generateSequence {
            generateOne()
        }
    }


    fun generateOne(
        summary: String = Arb.string().next(),
        description: String = Arb.string().next(),
        project: Project? = ProjectGen.generateDefault(),
        creator: User = UserGen.generateDefault(),
        issuetype: IssueType = IssueTypeGen.generateDefault(),
        fixVersions: Array<Version> = emptyArray(),
        attachment: Array<Attachment> = emptyArray(),
        created: LocalDateTime = LocalDateTime.now(),
        reporter: User? = UserGen.generateDefault(),
        assignee: User? = UserGen.generateDefault(),
        updated: LocalDateTime? = LocalDateTime.now(),
        status: Status = StatusGen.generateDefault(),
        priority: Priority = PriorityGen.generateDefault(),
        components: Array<JiraComponent> = emptyArray(),
        labels: Array<String> = emptyArray(),
        watchers: Watchers? = WatchersGen.generateDefault()
    ): IssueFields {
        return IssueFields(
            summary,
            description,
            project,
            creator,
            issuetype,
            fixVersions,
            attachment,
            created,
            reporter,
            assignee,
            updated,
            status,
            priority,
            components,
            labels,
            watchers
        )
    }

    fun random(seed: Long?): Sequence<IssueFields> {
        return random()
    }
}
