package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Attachment
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

/**
 * @author Mike Safonov
 */
class AttachmentGen {
    companion object {
        fun generateDefault(): Attachment {
            return AttachmentGen().generateOne()
        }

        fun empty(): Attachment? {
            return null
        }
    }

    fun constants(): Iterable<Attachment> {
        return emptyList()
    }

    fun random(): Sequence<Attachment> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        filename: String = Arb.string().next(),
        content: String = Arb.string().next()
    ): Attachment {
        return Attachment(filename, content)
    }
}
