package com.github.mikesafonov.jira.telegram.service.jira

import com.github.mikesafonov.jira.telegram.config.JiraOAuthProperties
import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl
import com.google.api.client.auth.oauth.OAuthParameters
import org.springframework.stereotype.Service

@Service
class JiraOAuthClient(private val oAuthTokenFactory: OAuthTokenFactory,
                      private val jiraOAuthProperties: JiraOAuthProperties) {

    private final val authorizationUrl : String

    init {
        authorizationUrl = "${jiraOAuthProperties.baseUrl}/plugins/servlet/oauth/authorize"
    }

    /**
     * Gets temporary request token and creates url to authorize it
     *
     * @return request token value, secret and authorize url
     */
    fun getAndAuthorizeTemporaryToken() : JiraTempTokenAndAuthorizeUrl{
        val temporaryToken = oAuthTokenFactory.getTempToken()
        val credentialsResponse = temporaryToken.execute()
        val authorizationURL = OAuthAuthorizeTemporaryTokenUrl(authorizationUrl)
        authorizationURL.temporaryToken = credentialsResponse.token

        return JiraTempTokenAndAuthorizeUrl(credentialsResponse.token, credentialsResponse.tokenSecret,
            authorizationURL.toString())

    }

    /**
     * Gets access token from JIRA
     *
     * @param tempToken    temporary request token
     * @param secret      secret (verification code provided by JIRA after request token authorization)
     * @return access token value
     */
    fun getAccessToken(tempToken : String, secret : String) : String{
        val accessToken = oAuthTokenFactory.getAccessToken(secret, tempToken)
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
        val oAuthAccessToken =
            oAuthTokenFactory.getAccessToken(secret, tmpToken)
        oAuthAccessToken.verifier = secret
        return oAuthAccessToken.createParameters()
    }
}