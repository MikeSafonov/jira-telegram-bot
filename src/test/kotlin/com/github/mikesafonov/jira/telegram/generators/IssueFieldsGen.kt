package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.*
import io.kotlintest.properties.Gen
import java.time.LocalDateTime

/**
 * @author Mike Safonov
 */

class IssueFieldsGen : Gen<IssueFields> {

    companion object {
        fun generateDefault(): IssueFields {
            return IssueFieldsGen().generateOne()
        }

        fun empty(): IssueFields? {
            return null
        }
    }


    override fun constants(): Iterable<IssueFields> {
        return emptyList()
    }

    override fun random(): Sequence<IssueFields> {
        return generateSequence {
            generateOne()
        }
    }


    fun generateOne(
        summary: String = Gen.string().random().first(),
        description: String = Gen.string().random().first(),
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
        watchers: Watchers = WatchersGen.generateDefault()
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
}
