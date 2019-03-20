package com.github.mikesafonov.jira.telegram.service.jira.oauth

import com.github.mikesafonov.jira.telegram.config.JiraOAuthProperties
import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl
import com.google.api.client.auth.oauth.OAuthParameters
import org.springframework.stereotype.Service

@Service
class JiraOAuthClient(
    private val factory: JiraOAuthTokenFactory,
    private val properties: JiraOAuthProperties
) {

    /**
     * Gets temporary request token and creates url to authorize it
     *
     * @return request token value, secret and authorize url
     */
    fun getAndAuthorizeTemporaryToken(): JiraTempTokenAndAuthorizeUrl {
        val temporaryToken = factory.getTempToken()
        val credentialsResponse = temporaryToken.execute()
        val authorizationURL = OAuthAuthorizeTemporaryTokenUrl(properties.authorizationUrl)
        authorizationURL.temporaryToken = credentialsResponse.token

        return JiraTempTokenAndAuthorizeUrl(
            credentialsResponse.token, credentialsResponse.tokenSecret,
            authorizationURL.toString()
        )
    }

    /**
     * Gets access token from JIRA
     *
     * @param tempToken temporary request token
     * @param secret      secret (verification code provided by JIRA after request token authorization)
     * @return access token value
     */
    fun getAccessToken(tempToken: String, secret: String): String {
        val accessToken = factory.getAccessToken(secret, tempToken)
        return accessToken.execute().token
    }

    /**
     * Creates OAuthParameters used to make authorized request to JIRA
     *
     * @param accessToken access token
     * @param secretToken secret token
     * @return jira oauth parameters
     */
    fun getParameters(accessToken: String, secretToken: String): OAuthParameters {
        return factory.getAccessToken(secretToken, accessToken).createParameters()
    }
}