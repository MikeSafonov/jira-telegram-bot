package com.github.mikesafonov.jira.telegram.service.jira

import com.github.mikesafonov.jira.telegram.config.conditional.ConditionalOnJiraOAuth
import com.github.mikesafonov.jira.telegram.service.jira.oauth.JiraOAuthClient
import com.github.mikesafonov.jira.telegram.service.jira.oauth.JiraTempTokenAndAuthorizeUrl
import com.google.api.client.auth.oauth.OAuthParameters
import org.springframework.stereotype.Service

/**
 * @author Mike Safonov
 */
@Service
@ConditionalOnJiraOAuth
class JiraAuthService(
    private val jiraOauthClient: JiraOAuthClient,
    private val authorizationService: AuthorizationService,
    private val tempTokenStorageService: TempTokenStorageService
) {

    fun isAuthorized(id: Long): Boolean {
        return authorizationService.get(id) != null
    }

    /**
     * Create temporary oauth token, secret token and access url. Storing secret to database,
     * storing temporary token to in memory map
     * @param id telegram id
     */
    fun createTemporaryToken(id: Long): JiraTempTokenAndAuthorizeUrl {
        val temporaryToken = jiraOauthClient.getAndAuthorizeTemporaryToken()
        tempTokenStorageService.put(id, temporaryToken.token)
        authorizationService.saveSecret(id, temporaryToken.secret)
        return temporaryToken
    }

    /**
     * Create access token from temporary token and verification code. Storing access token in database
     * @param id telegram id
     * @param verificationCode jira verification code
     */
    fun createAccessToken(id: Long, verificationCode: String) {
        val authorization =
            authorizationService.get(id) ?: throw JiraAuthorizationException("No secretToken token for chat $id")
        val tempToken = tempTokenStorageService.get(id) ?: throw JiraAuthorizationException("No temporary token for chat $id")

        val accessToken = jiraOauthClient.getAccessToken(tempToken, verificationCode)

        authorization.accessToken = accessToken
        authorizationService.save(authorization)
        tempTokenStorageService.remove(id)
    }

    /**
     * Create oauth request parameters for telegram id
     * @param id telegram id
     */
    fun getOAuthParameters(id: Long): OAuthParameters {
        val authorization =
            authorizationService.get(id) ?: throw JiraAuthorizationException("Chat $id not authorized in jira")
        val secretToken = authorization.secretToken
            ?: throw JiraAuthorizationException("Chat $id not authorized in jira. Secret token not found")
        val accessToken = authorization.accessToken
            ?: throw JiraAuthorizationException("Chat $id not authorized in jira. Access token not found")
        return jiraOauthClient.getParameters(accessToken, secretToken)
    }
}