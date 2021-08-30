package com.github.mikesafonov.jira.telegram.service

import com.github.difflib.text.DiffRowGenerator
import com.github.mikesafonov.jira.telegram.dao.TemplateParseMode
import com.github.mikesafonov.jira.telegram.escapeMarkdownV2Common

/**
 * @author Mike Safonov
 */

object DiffService {
    private val markdownTags = TagsPair("_", "*")
    private val markdownV2Tags = TagsPair("~", "*")
    private val htmlTags = TagsPair("<s>", "<b>", "</s>", "</b>")

    fun process(originalString: String, newString: String, parseMode: TemplateParseMode): String {
        val tags = getTagsForMode(parseMode)
        val generator = DiffRowGenerator.create()
            .showInlineDiffs(true)
            .mergeOriginalRevised(true)
            .ignoreWhiteSpaces(true)
            .inlineDiffByWord(true)
            .oldTag { isStart: Boolean? -> if(isStart!!) tags.oldStart else tags.oldEnd } //introduce markdown style for strikethrough
            .newTag { isStart: Boolean? -> if(isStart!!) tags.newStart else tags.newEnd } //introduce style for bold
            .build()
        val diff = generator.generateDiffRows(originalString.lines(), newString.lines());
        return postProcessValue(diff.joinToString("\n") { it.oldLine }, parseMode)
    }

    private fun postProcessValue(value: String, parseMode: TemplateParseMode) : String {
        return when(parseMode) {
            TemplateParseMode.MARKDOWN -> value
            TemplateParseMode.MARKDOWN_V2 -> value.escapeMarkdownV2Common()
            TemplateParseMode.HTML -> value
        }
    }

    private fun getTagsForMode(parseMode: TemplateParseMode): TagsPair {
        return when(parseMode) {
            TemplateParseMode.MARKDOWN -> markdownTags
            TemplateParseMode.MARKDOWN_V2 -> markdownV2Tags
            TemplateParseMode.HTML -> htmlTags
        }
    }


    data class TagsPair(val oldStart: String, val newStart: String,
                        val oldEnd: String = oldStart,
                        val newEnd: String = newStart)

}
