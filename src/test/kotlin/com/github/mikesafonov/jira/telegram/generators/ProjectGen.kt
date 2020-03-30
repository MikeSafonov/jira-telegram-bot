package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Project
import io.kotest.properties.Gen
import io.kotest.properties.long
import io.kotest.properties.string

/**
 * @author Mike Safonov
 */
class ProjectGen : Gen<Project> {
    companion object {
        fun generateDefault(): Project {
            return ProjectGen().generateOne()
        }

        fun empty(): Project? {
            return null
        }
    }

    override fun constants(): Iterable<Project> {
        return emptyList()
    }

    fun random(): Sequence<Project> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        id: Long = Gen.long().random().first(),
        self: String = Gen.string().random().first(),
        description: String = Gen.string().random().first(),
        name: String = Gen.string().random().first()
    ): Project {
        return Project(
            id, self, description, name
        )
    }

    override fun random(seed: Long?): Sequence<Project> {
        return random()
    }

}
