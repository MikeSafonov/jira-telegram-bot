package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.ChangelogItem
import io.kotlintest.properties.Gen

/**
 * @author Mike Safonov
 */

class ChangelogItemGen : Gen<ChangelogItem> {

    companion object {
        fun generate(): ChangelogItem {
            return ChangelogItemGen().random().first()
        }
    }


    override fun constants(): Iterable<ChangelogItem> {
        return emptyList()
    }

    override fun random(): Sequence<ChangelogItem> {
        return generateSequence {
            ChangelogItem(
                Gen.string().random().first(),
                Gen.string().random().first(),
                Gen.string().random().first()
            )
        }
    }

}