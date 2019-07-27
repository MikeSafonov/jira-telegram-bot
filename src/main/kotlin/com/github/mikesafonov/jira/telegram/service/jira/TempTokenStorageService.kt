package com.github.mikesafonov.jira.telegram.service.jira

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

/**
 * Storage for temporary jira oauth token based on [ConcurrentHashMap]
 * @author Mike Safonov
 */
@Service
class TempTokenStorageService {
    private final val tokenMap = ConcurrentHashMap<Long, String>()

    fun put(id: Long, value: String) {
        tokenMap[id] = value
    }

    fun remove(id: Long) {
        tokenMap.remove(id)
    }

    fun get(id: Long): String? {
        return tokenMap[id]
    }
}