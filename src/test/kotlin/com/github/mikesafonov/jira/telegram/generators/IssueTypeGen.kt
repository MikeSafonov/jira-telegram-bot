package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.IssueType
import io.kotlintest.properties.Gen

/**
 * @author Mike Safonov
 */

class IssueTypeGen : Gen<IssueType> {
    companion object {
        fun generateDefault(): IssueType {
            return IssueTypeGen().generateOne()
        }

        fun empty(): IssueType? {
            return null
        }
    }

    override fun constants(): Iterable<IssueType> {
        return emptyList()
    }

    override fun random(): Sequence<IssueType> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        name: String = Gen.string().random().first(),
        description: String = Gen.string().random().first()
    ): IssueType {
        return IssueType(name, description)
    }

}