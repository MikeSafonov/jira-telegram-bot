package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.ChangelogItem
import io.kotlintest.properties.Gen

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

    override fun random(): Sequence<ChangelogItem> {
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

}