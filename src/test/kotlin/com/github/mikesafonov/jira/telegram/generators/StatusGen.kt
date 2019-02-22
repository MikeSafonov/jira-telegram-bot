package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Status
import io.kotlintest.properties.Gen

/**
 * @author Mike Safonov
 */
class StatusGen : Gen<Status> {
    companion object {
        fun generate(): Status {
            return StatusGen().random().first()
        }

        fun empty(): Status? {
            return null
        }
    }

    override fun constants(): Iterable<Status> {
        return emptyList()
    }

    override fun random(): Sequence<Status> {
        return generateSequence {
            Status(Gen.string().random().first(), Gen.string().random().first(), Gen.string().random().first())
        }
    }

}