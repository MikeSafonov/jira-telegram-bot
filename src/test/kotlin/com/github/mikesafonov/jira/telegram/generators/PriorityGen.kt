package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Priority
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string


/**
 * @author Mike Safonov
 */
class PriorityGen {
    companion object {
        fun generateDefault(): Priority {
            return PriorityGen().generateOne()
        }

        fun empty(): Priority? {
            return null
        }
    }

    fun constants(): Iterable<Priority> {
        return emptyList()
    }

    fun random(): Sequence<Priority> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(name : String = Arb.string().next(),
                    iconUrl : String = Arb.string().next()) : Priority{
        return Priority(name, iconUrl)
    }

    fun random(seed: Long?): Sequence<Priority> {
        return random()
    }

}
