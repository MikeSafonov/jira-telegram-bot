package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.IssueEventTypeName
import io.kotlintest.properties.Gen

class IssueEventTypeNameGen : Gen<IssueEventTypeName> {
    override fun constants(): Iterable<IssueEventTypeName> {
        return emptyList()
    }

    override fun random(): Sequence<IssueEventTypeName> {
        return Gen.enum<IssueEventTypeName>().random()
    }
}