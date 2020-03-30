package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Comment
import com.github.mikesafonov.jira.telegram.dto.User
import io.kotest.properties.Gen
import io.kotest.properties.string
import java.time.LocalDateTime

/**
 * @author Mike Safonov
 */
class CommentGen : Gen<Comment> {

    companion object {
        fun generateDefault(): Comment {
            return CommentGen().generateOne()
        }

        fun empty(): Comment? {
            return null
        }
    }

    override fun constants(): Iterable<Comment> {
        return emptyList()
    }

    fun random(): Sequence<Comment> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        body: String = Gen.string().random().first(),
        author: User? = UserGen.generateDefault(),
        created: LocalDateTime = LocalDateTime.now(),
        updateAuthor: User? = UserGen.generateDefault(),
        self: String = Gen.string().random().first(),
        updated: LocalDateTime? = LocalDateTime.now()
    ): Comment {
        return Comment(body, author, created, updateAuthor, self, updated)
    }

    override fun random(seed: Long?): Sequence<Comment> {
        return random()
    }

}
