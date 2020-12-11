package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Changelog
import com.github.mikesafonov.jira.telegram.dto.ChangelogItem
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

class ChangelogGen {

    companion object {
        fun generateDefault(): Changelog {
            return ChangelogGen().generateOne()
        }

        fun empty(): Changelog? {
            return null
        }
    }

    fun constants(): Iterable<Changelog> {
        return emptyList()
    }

    fun random(): Sequence<Changelog> {
        return generateSequence { generateOne() }
    }

    fun generateOne(
        id: String = Arb.string().next(),
        items: Array<ChangelogItem> = emptyArray()
    ): Changelog {
        return Changelog(id, items)
    }

    fun random(seed: Long?): Sequence<Changelog> {
        return random()
    }
}
