package com.github.mikesafonov.jira.telegram.service.jira

import com.github.mikesafonov.jira.telegram.dao.Authorization
import com.github.mikesafonov.jira.telegram.dao.AuthorizationRepository
import org.springframework.stereotype.Service

/**
 * @author Mike Safonov
 */
@Service
class AuthorizationService(private val authorizationRepository: AuthorizationRepository) {

    /**
     * Search authorization by telegram id
     * @param id telegram id
     */
    fun get(id: Long): Authorization? {
        return authorizationRepository.findById(id).orElse(null)
    }

    /**
     * Save secret token to authorization for telegram id. Create new row if no row in database was found for specific
     * telegram id
     * @param id telegram id
     * @param secret secret token
     */
    fun saveSecret(id: Long, secret: String): Authorization {
        val authorization = get(id)
        return if (authorization == null) {
            authorizationRepository.save(Authorization(id, null, secret))
        } else {
            authorization.secretToken = secret
            authorizationRepository.save(authorization)
        }
    }

    fun save(authorization: Authorization): Authorization {
        return authorizationRepository.save(authorization)
    }
}