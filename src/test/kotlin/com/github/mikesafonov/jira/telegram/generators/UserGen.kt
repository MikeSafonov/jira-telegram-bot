package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.User
import io.kotlintest.properties.Gen

/**
 * @author Mike Safonov
 */
class UserGen : Gen<User> {
    companion object {
        fun generate(): User {
            return UserGen().random().first()
        }

        fun empty(): User? {
            return null
        }
    }

    override fun constants(): Iterable<User> {
        return emptyList()
    }

    override fun random(): Sequence<User> {
        return generateSequence {
            User(Gen.string().random().first(), Gen.string().random().first())
        }
    }

}