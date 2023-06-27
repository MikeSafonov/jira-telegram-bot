package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.FilterSubscription
import com.github.mikesafonov.jira.telegram.dao.FilterSubscriptionId
import com.github.mikesafonov.jira.telegram.dao.FilterSubscriptionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FilterSubscriptionService(
    private val repository: FilterSubscriptionRepository
) {

    fun getAll(): List<FilterSubscription> {
        return repository.findAll()
    }

    fun addSubscription(idFilter: Long, chat: Chat) {
        repository.save(FilterSubscription(FilterSubscriptionId(idFilter, chat)))
    }

    @Transactional
    fun deleteAllSubscriptions(idFilter: Long) {
        repository.deleteByIdFilter(idFilter)
    }
}