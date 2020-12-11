package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.User
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

/**
 * @author Mike Safonov
 */
class UserGen {
    companion object {
        fun generateDefault(): User {
            return UserGen().generateOne()
        }

        fun empty(): User? {
            return null
        }
    }

    fun constants(): Iterable<User> {
        return emptyList()
    }

    fun random(): Sequence<User> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        name: String = Arb.string().next(),
        displayName: String = Arb.string().next()
    ): User {
        return User(name, displayName)
    }

    fun random(seed: Long?): Sequence<User> {
        return random()
    }

}
