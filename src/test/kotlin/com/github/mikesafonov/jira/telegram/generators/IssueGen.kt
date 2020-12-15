package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Issue
import com.github.mikesafonov.jira.telegram.dto.IssueFields
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

/**
 * @author Mike Safonov
 */
class IssueGen {
    companion object {
        fun generateDefault(): Issue {
            return IssueGen().random().first()
        }


        fun empty(): Issue? {
            return null
        }
    }

    fun constants(): Iterable<Issue> {
        return emptyList()
    }

    fun random(): Sequence<Issue> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        id: Long = Arb.long().next(),
        self: String = randomString(),
        key: String = randomString(),
        issueFields: IssueFields = IssueFieldsGen.generateDefault()
    ): Issue {
        return Issue(id, self, key, issueFields)
    }

    private fun randomString(): String {
        return Arb.string().next()
    }
}
