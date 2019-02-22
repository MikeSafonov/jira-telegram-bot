package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.IssueType
import io.kotlintest.properties.Gen

/**
 * @author Mike Safonov
 */

class IssueTypeGen : Gen<IssueType> {
    companion object {
        fun generate(): IssueType {
            return IssueTypeGen().random().first()
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
            IssueType(Gen.string().random().first(), Gen.string().random().first())
        }
    }

}