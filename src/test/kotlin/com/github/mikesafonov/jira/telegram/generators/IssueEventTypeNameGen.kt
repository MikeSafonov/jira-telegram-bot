package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.IssueEventTypeName
import io.kotest.properties.Gen
import io.kotest.properties.enum

class IssueEventTypeNameGen : Gen<IssueEventTypeName> {
    override fun constants(): Iterable<IssueEventTypeName> {
        return emptyList()
    }

    fun random(): Sequence<IssueEventTypeName> {
        return Gen.enum<IssueEventTypeName>().random()
    }

    override fun random(seed: Long?): Sequence<IssueEventTypeName> {
        return random()
    }
}
