package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Attachment
import io.kotest.properties.Gen
import io.kotest.properties.string

/**
 * @author Mike Safonov
 */
class AttachmentGen : Gen<Attachment> {
    companion object {
        fun generateDefault(): Attachment {
            return AttachmentGen().generateOne()
        }

        fun empty(): Attachment? {
            return null
        }
    }

    override fun constants(): Iterable<Attachment> {
        return emptyList()
    }

    fun random(): Sequence<Attachment> {
        return generateSequence {
            generateOne()
        }
    }

    override fun random(seed: Long?): Sequence<Attachment> {
        return random()
    }

    fun generateOne(
        filename: String = Gen.string().random().first(),
        content: String = Gen.string().random().first()
    ): Attachment {
        return Attachment(filename, content)
    }
}
