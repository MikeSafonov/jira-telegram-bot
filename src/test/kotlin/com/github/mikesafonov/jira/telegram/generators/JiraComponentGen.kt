package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.JiraComponent
import io.kotlintest.properties.Gen

/**
 * @author Mike Safonov
 */
class JiraComponentGen : Gen<JiraComponent> {
    companion object {
        fun generate(): JiraComponent {
            return JiraComponentGen().random().first()
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
            JiraComponent(Gen.string().random().first(), Gen.string().random().first())
        }
    }

}