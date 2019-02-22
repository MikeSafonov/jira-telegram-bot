package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Comment
import com.github.mikesafonov.jira.telegram.dto.User
import io.kotlintest.properties.Gen
import java.time.LocalDateTime

/**
 * @author Mike Safonov
 */
class CommentGen : Gen<Comment> {

    companion object {
        fun generate(): Comment {
            return CommentGen().random().first()
        }

        fun empty(): Comment? {
            return null
        }

        fun withUsers(user : User? = null, updateUser : User? = null) : Comment{
            return Comment(
                Gen.string().random().first(),
                user,
                LocalDateTime.now(),
                updateUser,
                Gen.string().random().first(),
                LocalDateTime.now()
            )
        }

    }

    override fun constants(): Iterable<Comment> {
        return emptyList()
    }

    override fun random(): Sequence<Comment> {
        return generateSequence {
            Comment(
                Gen.string().random().first(),
                UserGen.generate(),
                LocalDateTime.now(),
                UserGen.generate(),
                Gen.string().random().first(),
                LocalDateTime.now()
            )
        }
    }

}