package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.IssueType
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

/**
 * @author Mike Safonov
 */

class IssueTypeGen {
    companion object {
        fun generateDefault(): IssueType {
            return IssueTypeGen().generateOne()
        }

        fun empty(): IssueType? {
            return null
        }
    }

    fun constants(): Iterable<IssueType> {
        return emptyList()
    }

    fun random(): Sequence<IssueType> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        name: String = Arb.string().next(),
        description: String = Arb.string().next()
    ): IssueType {
        return IssueType(name, description)
    }

    fun random(seed: Long?): Sequence<IssueType> {
        return random()
    }

}
