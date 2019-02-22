package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Attachment
import io.kotlintest.properties.Gen

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

    override fun random(): Sequence<Attachment> {
        return generateSequence {
            generateOne()
        }
    }

    fun generateOne(
        filename: String = Gen.string().random().first(),
        content: String = Gen.string().random().first()
    ): Attachment {
        return Attachment(filename, content)
    }
}