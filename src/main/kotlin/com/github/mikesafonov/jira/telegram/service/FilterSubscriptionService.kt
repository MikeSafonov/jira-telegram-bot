package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.dao.FilterSubscription
import com.github.mikesafonov.jira.telegram.dao.FilterSubscriptionRepository
import org.springframework.stereotype.Service

@Service
class FilterSubscriptionService(
    private val repository: FilterSubscriptionRepository
) {

    fun getAll(): List<FilterSubscription> {
        return repository.findAll()
    }
}