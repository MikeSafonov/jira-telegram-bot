package com.github.mikesafonov.jira.telegram.service.jira.oauth

import com.github.mikesafonov.jira.telegram.config.JiraOAuthProperties
import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl
import com.google.api.client.auth.oauth.OAuthParameters
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class JiraOAuthClient(
    private val factory: JiraOAuthTokenFactory,
    private val properties: JiraOAuthProperties
) {

    private final val tokenMap = ConcurrentHashMap<String, String>()

    /**
     * Gets temporary request token and creates url to authorize it
     *
     * @param id telegram id
     * @return request token value, secret and authorize url
     */
    fun getAndAuthorizeTemporaryToken(id: String): JiraTempTokenAndAuthorizeUrl {
        val temporaryToken = factory.getTempToken()
        val credentialsResponse = temporaryToken.execute()
        val authorizationURL = OAuthAuthorizeTemporaryTokenUrl(properties.authorizationUrl)
        authorizationURL.temporaryToken = credentialsResponse.token

        tokenMap[id] = credentialsResponse.token

        return JiraTempTokenAndAuthorizeUrl(
            credentialsResponse.token, credentialsResponse.tokenSecret,
            authorizationURL.toString()
        )
    }

    /**
     * Gets access token from JIRA
     *
     * @param id telegram id    temporary request token
     * @param secret      secret (verification code provided by JIRA after request token authorization)
     * @return access token value
     */
    fun getAccessToken(id: String, secret: String): String {
        val tempToken = tokenMap[id] ?: throw RuntimeException("No temp token for chat $id")
        val accessToken = factory.getAccessToken(secret, tempToken)
        return accessToken.execute().token
    }

    /**
     * Creates OAuthParameters used to make authorized request to JIRA
     *
     * @param tmpToken
     * @param secret
     * @return
     */
    fun getParameters(tmpToken: String, secret: String): OAuthParameters {
        val accessToken = factory.getAccessToken(secret, tmpToken)
        return accessToken.createParameters()
    }
}