package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.IssueEventTypeName
import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.take

class IssueEventTypeNameGen {
    fun constants(): Iterable<IssueEventTypeName> {
        return emptyList()
    }

    fun random(): Sequence<IssueEventTypeName> {
        return Arb.enum<IssueEventTypeName>().take(20)
    }
}
