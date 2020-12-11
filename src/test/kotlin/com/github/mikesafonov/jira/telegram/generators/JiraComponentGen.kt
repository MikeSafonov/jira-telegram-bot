package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.JiraComponent
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

/**
 * @author Mike Safonov
 */
class JiraComponentGen  {
    companion object {
        fun generateDefault(): JiraComponent {
            return JiraComponentGen().generateOne()
        }

        fun empty(): JiraComponent? {
            return null
        }
    }

    fun constants(): Iterable<JiraComponent> {
        return emptyList()
    }

    fun random(): Sequence<JiraComponent> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        self: String = Arb.string().next(),
        name: String = Arb.string().next()
    ): JiraComponent {
        return JiraComponent(self, name)
    }

    fun random(seed: Long?): Sequence<JiraComponent> {
        return random()
    }

}
