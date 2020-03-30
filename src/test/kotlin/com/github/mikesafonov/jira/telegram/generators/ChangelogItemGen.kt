package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.ChangelogItem
import io.kotest.properties.Gen
import io.kotest.properties.string

/**
 * @author Mike Safonov
 */

class ChangelogItemGen : Gen<ChangelogItem> {

    companion object {
        fun generateDefault(): ChangelogItem {
            return ChangelogItemGen().generateOne()
        }

        fun empty(): ChangelogItem? {
            return null
        }
    }


    override fun constants(): Iterable<ChangelogItem> {
        return emptyList()
    }

    fun random(): Sequence<ChangelogItem> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        field: String = Gen.string().random().first(),
        fromString: String? = Gen.string().random().first(),
        newString: String? = Gen.string().random().first()
    ): ChangelogItem {
        return ChangelogItem(field, fromString, newString)
    }

    override fun random(seed: Long?): Sequence<ChangelogItem> {
        return random()
    }

}
