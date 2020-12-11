package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Version
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

/**
 * @author Mike Safonov
 */
class VersionGen {

    companion object {
        fun generateDefault(): Version {
            return VersionGen().generateOne()
        }

        fun empty(): Version? {
            return null
        }
    }

    fun constants(): Iterable<Version> {
        return emptyList()
    }

    fun random(): Sequence<Version> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        id: Long = Arb.long().next(),
        self: String = randomString(),
        description: String = randomString(),
        name: String = randomString(),
        archived: Boolean = Arb.bool().next(),
        released: Boolean = Arb.bool().next()
    ): Version {
        return Version(
            id, self, description, name, archived, released
        )
    }

    private fun randomString(): String {
        return Arb.string().next()
    }

    fun random(seed: Long?): Sequence<Version> {
        return random()
    }

}
