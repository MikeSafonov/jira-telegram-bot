package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Issue
import com.github.mikesafonov.jira.telegram.dto.IssueFields
import io.kotest.properties.Gen
import io.kotest.properties.long
import io.kotest.properties.string

/**
 * @author Mike Safonov
 */
class IssueGen : Gen<Issue> {
    companion object {
        fun generateDefault(): Issue {
            return IssueGen().random().first()
        }


        fun empty(): Issue? {
            return null
        }
    }

    override fun constants(): Iterable<Issue> {
        return emptyList()
    }

    fun random(): Sequence<Issue> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        id: Long = Gen.long().random().first(),
        self: String = randomString(),
        key: String = randomString(),
        issueFields: IssueFields = IssueFieldsGen.generateDefault()
    ): Issue {
        return Issue(id, self, key, issueFields)
    }

    private fun randomString(): String {
        return Gen.string().random().first()
    }

    override fun random(seed: Long?): Sequence<Issue> {
        return random()
    }

}
