package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Issue
import com.github.mikesafonov.jira.telegram.dto.IssueFields
import io.kotlintest.properties.Gen

/**
 * @author Mike Safonov
 */
class IssueGen : Gen<Issue> {
    companion object {
        fun generate(): Issue {
            return IssueGen().random().first()
        }


        fun empty(): Issue? {
            return null
        }
    }

    override fun constants(): Iterable<Issue> {
        return emptyList()
    }

    override fun random(): Sequence<Issue> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        id: Long = Gen.long().random().first(), self: String = randomString(),
        key: String = randomString(),
        issueFields: IssueFields = IssueFieldsGen.generate()
    ): Issue {
        return Issue(id, self, key, issueFields)
    }

    private fun randomString(): String {
        return Gen.string().random().first()
    }

}