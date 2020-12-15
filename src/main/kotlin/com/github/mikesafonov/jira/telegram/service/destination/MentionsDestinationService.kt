package com.github.mikesafonov.jira.telegram.service.destination

import com.github.mikesafonov.jira.telegram.dto.Comment
import com.github.mikesafonov.jira.telegram.dto.Event
import org.springframework.stereotype.Service

/**
 * Collects mentions from events comment
 * @author Mike Safonov
 */
@Service
class MentionsDestinationService : DestinationDetectorService {
    override fun findDestinations(event: Event): List<String> {
        return getMentionsFromComment(event.comment)
    }

    private fun getMentionsFromComment(comment: Comment?): List<String> {
        if (comment == null) {
            return emptyList()
        }
        val regex = "(?<=\\[~)(.*?)(?=\\])".toRegex()
        val mentions = ArrayList<String>()
        regex.findAll(comment.body).iterator().forEach {
            mentions.add(it.value)
        }
        return mentions
    }
}
