package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.IssueFields
import io.kotlintest.properties.Gen
import java.time.LocalDateTime

/**
 * @author Mike Safonov
 */

class IssueFieldsGen : Gen<IssueFields> {

    companion object {
        fun generate(): IssueFields {
            return IssueFieldsGen().random().first()
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
            IssueFields(
                Gen.string().random().first(),
                Gen.string().random().first(),
                ProjectGen.empty(),
                UserGen.generate(),
                IssueTypeGen.generate(),
                emptyArray(),
                emptyArray(),
                LocalDateTime.now(),
                UserGen.empty(),
                UserGen.empty(),
                LocalDateTime.now(),
                StatusGen.generate(),
                PriorityGen.generate()
            )
        }
    }
}