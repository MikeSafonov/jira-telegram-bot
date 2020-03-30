package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Changelog
import com.github.mikesafonov.jira.telegram.dto.ChangelogItem
import io.kotest.properties.Gen
import io.kotest.properties.string

class ChangelogGen : Gen<Changelog> {

    companion object {
        fun generateDefault(): Changelog {
            return ChangelogGen().generateOne()
        }

        fun empty(): Changelog? {
            return null
        }
    }

    override fun constants(): Iterable<Changelog> {
        return emptyList()
    }

    fun random(): Sequence<Changelog> {
        return generateSequence { generateOne() }
    }

    fun generateOne(
        id: String = Gen.string().random().first(),
        items: Array<ChangelogItem> = emptyArray()
    ): Changelog {
        return Changelog(id, items)
    }

    override fun random(seed: Long?): Sequence<Changelog> {
        return random()
    }
}
