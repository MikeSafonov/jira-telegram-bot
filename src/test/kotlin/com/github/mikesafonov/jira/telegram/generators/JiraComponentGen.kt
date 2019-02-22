package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.JiraComponent
import io.kotlintest.properties.Gen

/**
 * @author Mike Safonov
 */
class JiraComponentGen : Gen<JiraComponent> {
    companion object {
        fun generateDefault(): JiraComponent {
            return JiraComponentGen().generateOne()
        }

        fun empty(): JiraComponent? {
            return null
        }
    }

    override fun constants(): Iterable<JiraComponent> {
        return emptyList()
    }

    override fun random(): Sequence<JiraComponent> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        self: String = Gen.string().random().first(),
        name: String = Gen.string().random().first()
    ): JiraComponent {
        return JiraComponent(self, name)
    }

}