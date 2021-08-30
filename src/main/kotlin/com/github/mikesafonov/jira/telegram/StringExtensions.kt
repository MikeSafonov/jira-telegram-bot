package com.github.mikesafonov.jira.telegram

/**
 * @author Mike Safonov
 */
private val markdownV2CommonEscapes = setOf(
    '_',
    '*',
    '[', ']',
    '(', ')',
    '~',
    '`',
    '>',
    '#',
    '+', '-', '=',
    '|',
    '{', '}',
    '.', '!'
)

private fun String.escapeMarkdownV2(escapeCharacters: Iterable<Char>): String = map {
    if (it in escapeCharacters) {
        "\\$it"
    } else {
        "$it"
    }
}.joinToString("")

fun String.escapeMarkdownV2Common() = escapeMarkdownV2(markdownV2CommonEscapes)
