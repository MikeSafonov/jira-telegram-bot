package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Project
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

/**
 * @author Mike Safonov
 */
class ProjectGen {
    companion object {
        fun generateDefault(): Project {
            return ProjectGen().generateOne()
        }

        fun empty(): Project? {
            return null
        }
    }

    fun constants(): Iterable<Project> {
        return emptyList()
    }

    fun random(): Sequence<Project> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        id: Long = Arb.long().next(),
        self: String = Arb.string().next(),
        description: String = Arb.string().next(),
        name: String = Arb.string().next()
    ): Project {
        return Project(
            id, self, description, name
        )
    }

    fun random(seed: Long?): Sequence<Project> {
        return random()
    }

}
