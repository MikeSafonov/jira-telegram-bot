package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Version
import io.kotlintest.properties.Gen

/**
 * @author Mike Safonov
 */
class VersionGen : Gen<Version> {

    companion object {
        fun generateDefault(): Version {
            return VersionGen().generateOne()
        }

        fun empty(): Version? {
            return null
        }
    }

    override fun constants(): Iterable<Version> {
        return emptyList()
    }

    override fun random(): Sequence<Version> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        id: Long = Gen.long().random().first(),
        self: String = randomString(),
        description: String = randomString(),
        name: String = randomString(),
        archived: Boolean = Gen.bool().random().first(),
        released: Boolean = Gen.bool().random().first()
    ): Version {
        return Version(
            id, self, description, name, archived, released
        )
    }

    private fun randomString(): String {
        return Gen.string().random().first()
    }

}