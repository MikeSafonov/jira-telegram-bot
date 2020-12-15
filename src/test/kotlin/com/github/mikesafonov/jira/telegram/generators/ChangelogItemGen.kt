package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.ChangelogItem
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

/**
 * @author Mike Safonov
 */

class ChangelogItemGen {

    companion object {
        fun generateDefault(): ChangelogItem {
            return ChangelogItemGen().generateOne()
        }

        fun empty(): ChangelogItem? {
            return null
        }
    }


    fun constants(): Iterable<ChangelogItem> {
        return emptyList()
    }

    fun random(): Sequence<ChangelogItem> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        field: String = Arb.string().next(),
        fromString: String? = Arb.string().next(),
        newString: String? = Arb.string().next()
    ): ChangelogItem {
        return ChangelogItem(field, fromString, newString)
    }
}
