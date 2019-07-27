package com.github.mikesafonov.jira.telegram.service.jira

/**
 * Builder class for JQL search string
 * @author Mike Safonov
 */
class JQLBuilder {
    private val stringBuilder = StringBuffer()

    companion object {
        fun builder(): JQLBuilder {
            return JQLBuilder()
        }
    }

    /**
     * Add __resolution = Unresolved__ condition to JQL search string
     */
    fun unresolved(): JQLBuilder {
        if (stringBuilder.isEmpty()) {
            stringBuilder.append("resolution = Unresolved ")
        } else {
            stringBuilder.append("and resolution = Unresolved ")
        }
        return this
    }

    /**
     * Add __assignee__ condition to JQL search string
     * @param assignee assignee
     */
    fun assignedTo(assignee: String): JQLBuilder {
        if (stringBuilder.isEmpty()) {
            stringBuilder.append("assignee = $assignee ")
        } else {
            stringBuilder.append("and assignee = $assignee ")
        }
        return this
    }

    /**
     * Add order by __createdDate__ to JQL search string
     * @param asc order ascending or descending
     */
    fun orderByDateCreate(asc: Boolean = false): JQLBuilder {
        if (asc) {
            stringBuilder.append("ORDER BY createdDate ASC")
        } else {
            stringBuilder.append("ORDER BY createdDate")
        }
        return this
    }

    fun build(): String {
        return stringBuilder.toString()
    }
}