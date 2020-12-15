package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Comment
import com.github.mikesafonov.jira.telegram.dto.User
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import java.time.LocalDateTime

/**
 * @author Mike Safonov
 */
class CommentGen {

    companion object {
        fun generateDefault(): Comment {
            return CommentGen().generateOne()
        }

        fun empty(): Comment? {
            return null
        }
    }

    fun constants(): Iterable<Comment> {
        return emptyList()
    }

    fun random(): Sequence<Comment> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        body: String = Arb.string().next(),
        author: User? = UserGen.generateDefault(),
        created: LocalDateTime = LocalDateTime.now(),
        updateAuthor: User? = UserGen.generateDefault(),
        self: String = Arb.string().next(),
        updated: LocalDateTime? = LocalDateTime.now()
    ): Comment {
        return Comment(body, author, created, updateAuthor, self, updated)
    }
}
