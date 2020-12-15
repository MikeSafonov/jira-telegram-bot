package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Status
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

/**
 * @author Mike Safonov
 */
class StatusGen {
    companion object {
        fun generateDefault(): Status {
            return StatusGen().generateOne()
        }

        fun empty(): Status? {
            return null
        }
    }

    fun constants(): Iterable<Status> {
        return emptyList()
    }

    fun random(): Sequence<Status> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        id: String = Arb.string().next(),
        description: String = Arb.string().next(),
        name: String = Arb.string().next()
    ): Status {
        return Status(id, description, name)
    }
}
