package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Attachment
import com.github.mikesafonov.jira.telegram.dto.Watchers
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

/**
 * @author Mike Safonov
 */
class WatchersGen {
    companion object {
        fun generateDefault(): Watchers {
            return WatchersGen().generateOne()
        }

        fun empty(): Attachment? {
            return null
        }
    }

    fun constants(): Iterable<Watchers> {
        return emptyList()
    }

    fun random(): Sequence<Watchers> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        self: String = Arb.string().next(),
        watchCount: Int = Arb.int().next(),
        isWatching: Boolean = Arb.bool().next()
    ): Watchers {
        return Watchers(self, watchCount, isWatching)
    }
}
