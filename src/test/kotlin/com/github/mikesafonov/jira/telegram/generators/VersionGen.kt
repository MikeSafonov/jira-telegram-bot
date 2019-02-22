package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Version
import io.kotlintest.properties.Gen

/**
 * @author Mike Safonov
 */
class VersionGen : Gen<Version> {

    companion object {
        fun generate(): Version {
            return VersionGen().random().first()
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
            Version(
                Gen.long().random().first(),
                Gen.string().random().first(),
                Gen.string().random().first(),
                Gen.string().random().first(),
                Gen.bool().random().first(),
                Gen.bool().random().first()
            )
        }
    }

}