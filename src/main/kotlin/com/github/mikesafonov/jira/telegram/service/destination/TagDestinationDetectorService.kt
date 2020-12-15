package com.github.mikesafonov.jira.telegram.service.destination

import com.github.mikesafonov.jira.telegram.dto.Comment
import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.service.ChatService
import com.github.mikesafonov.jira.telegram.service.TagService
import org.springframework.stereotype.Service

/**
 * Collects logins by tags from events comment
 * @author Mike Safonov
 */
@Service
class TagDestinationDetectorService(private val tagService: TagService, private val chatService: ChatService) :
    DestinationDetectorService {

    override fun findDestinations(event: Event): List<String> {
        val tags = getTagsFromComment(event.comment)
        if(tags.contains("everyone")) {
            return chatService.getAllLogins()
        }
        return tags.minus("everyone").flatMap { tagService.getJiraLoginsByTagKey(it) }
    }

    private fun getTagsFromComment(comment: Comment?): List<String> {
        if (comment == null) {
            return emptyList()
        }
        val regex = "\\B@\\w+".toRegex()
        val mentions = ArrayList<String>()
        regex.findAll(comment.body).iterator().forEach {
            mentions.add(it.value.removePrefix("@"))
        }
        return mentions
    }
}
