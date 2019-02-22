package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Priority
import io.kotlintest.properties.Gen

/**
 * @author Mike Safonov
 */
class PriorityGen : Gen<Priority> {
    companion object {
        fun generate(): Priority {
            return PriorityGen().random().first()
        }

        fun empty(): Priority? {
            return null
        }
    }

    override fun constants(): Iterable<Priority> {
        return emptyList()
    }

    override fun random(): Sequence<Priority> {
        return generateSequence {
            Priority(Gen.string().random().first(), Gen.string().random().first())
        }
    }

}