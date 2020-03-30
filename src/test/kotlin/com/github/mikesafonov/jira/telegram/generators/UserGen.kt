package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.User
import io.kotest.properties.Gen
import io.kotest.properties.string

/**
 * @author Mike Safonov
 */
class UserGen : Gen<User> {
    companion object {
        fun generateDefault(): User {
            return UserGen().generateOne()
        }

        fun empty(): User? {
            return null
        }
    }

    override fun constants(): Iterable<User> {
        return emptyList()
    }

    fun random(): Sequence<User> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        name: String = Gen.string().random().first(),
        displayName: String = Gen.string().random().first()
    ): User {
        return User(name, displayName)
    }

    override fun random(seed: Long?): Sequence<User> {
        return random()
    }

}
