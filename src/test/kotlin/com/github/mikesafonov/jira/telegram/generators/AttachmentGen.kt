package com.github.mikesafonov.jira.telegram.generators

import com.github.mikesafonov.jira.telegram.dto.Attachment
import io.kotlintest.properties.Gen

/**
 * @author Mike Safonov
 */
class AttachmentGen : Gen<Attachment> {
    companion object {
        fun generate(): Attachment {
            return AttachmentGen().random().first()
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
            Attachment(Gen.string().random().first(), Gen.string().random().first())
        }
    }

}