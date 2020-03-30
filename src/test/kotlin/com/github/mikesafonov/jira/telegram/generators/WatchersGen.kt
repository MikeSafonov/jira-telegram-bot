package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Attachment
import com.github.mikesafonov.jira.telegram.dto.Watchers
import io.kotest.properties.Gen
import io.kotest.properties.bool
import io.kotest.properties.int
import io.kotest.properties.string

/**
 * @author Mike Safonov
 */
class WatchersGen : Gen<Watchers> {
    companion object {
        fun generateDefault(): Watchers {
            return WatchersGen().generateOne()
        }

        fun empty(): Attachment? {
            return null
        }
    }

    override fun constants(): Iterable<Watchers> {
        return emptyList()
    }

    fun random(): Sequence<Watchers> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        self: String = Gen.string().random().first(),
        watchCount: Int = Gen.int().random().first(),
        isWatching: Boolean = Gen.bool().random().first()
    ): Watchers {
        return Watchers(self, watchCount, isWatching)
    }

    override fun random(seed: Long?): Sequence<Watchers> {
        return random()
    }
}
