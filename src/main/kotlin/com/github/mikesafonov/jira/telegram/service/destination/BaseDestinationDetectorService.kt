package com.github.mikesafonov.jira.telegram.service.destination

import com.github.mikesafonov.jira.telegram.dto.*

/**
 * @author Mike Safonov
 */
abstract class BaseDestinationDetectorService : DestinationDetectorService {

    protected fun requiredDestinations(event: Event): List<String> {
        if (event.issue != null && event.issueEventTypeName != null) {
            return when (event.issueEventTypeName) {
                IssueEventTypeName.ISSUE_COMMENTED -> {
                    allIssueUsersWithoutInitiator(event.issue, event.comment?.author, event.comment)
                }
                IssueEventTypeName.ISSUE_CREATED, IssueEventTypeName.ISSUE_GENERIC,
                IssueEventTypeName.ISSUE_UPDATED, IssueEventTypeName.ISSUE_COMMENT_EDITED,
                IssueEventTypeName.ISSUE_COMMENT_DELETED, IssueEventTypeName.ISSUE_ASSIGNED -> {
                    allIssueUsersWithoutInitiator(event.issue, event.user, event.comment)
                }
            }
        }
        return emptyList()
    }

    /**
     * Collect **creator**, **reporter** and **assignee** logins from [issue] to list, ignoring *null* values and filtered
     * by [initiator] login if present.
     * @param issue jira issue
     * @param initiator user who fired this issues event
     * @return list of logins
     */
    protected abstract fun allIssueUsersWithoutInitiator(issue: Issue, initiator: User?, comment: Comment?): List<String>

    /**
     * Collect **creator**, **reporter** and **assignee** logins from [issue] to list, ignoring *null* values
     * @param issue jira issue
     * @return list of logins
     */
    protected fun allIssueUsers(issue: Issue): List<String> {
        return listOfNotNull(issue.creatorName, issue.reporterName, issue.assigneeName)
    }


    protected fun getMentionsFromComment(comment: Comment?): List<String> {
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
