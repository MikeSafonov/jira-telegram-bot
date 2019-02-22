package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Project
import io.kotlintest.properties.Gen

/**
 * @author Mike Safonov
 */
class ProjectGen : Gen<Project> {
    companion object {
        fun generate(): Project {
            return ProjectGen().random().first()
        }

        fun empty(): Project? {
            return null
        }
    }

    override fun constants(): Iterable<Project> {
        return emptyList()
    }

    override fun random(): Sequence<Project> {
        return generateSequence {
            Project(
                Gen.long().random().first(),
                Gen.string().random().first(),
                Gen.string().random().first(),
                Gen.string().random().first()
            )
        }
    }

}