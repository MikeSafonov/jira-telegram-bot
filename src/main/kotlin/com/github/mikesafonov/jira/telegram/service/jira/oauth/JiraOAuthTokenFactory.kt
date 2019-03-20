package com.github.mikesafonov.jira.telegram.service.jira.oauth

import com.github.mikesafonov.jira.telegram.config.JiraOAuthProperties
import com.google.api.client.auth.oauth.OAuthRsaSigner
import com.google.api.client.http.javanet.NetHttpTransport
import org.springframework.stereotype.Service

@Service
class JiraOAuthTokenFactory(
    private val jiraOAuthProperties: JiraOAuthProperties,
    keyReader: KeyReader
) {
    private val privateKey = keyReader.readPrivateRsa(jiraOAuthProperties.privateKey)

    /**
     * Initialize JiraOAuthGetAccessToken
     * by setting it to use POST method, secret, request token
     * and setting consumer and private keys.
     *
     * @param tempToken    request token
     * @param secret      secret (verification code provided by JIRA after request token authorization)
     * @return JiraOAuthGetAccessToken request
     */
    fun getAccessToken(
        secret: String, tempToken: String
    ): JiraOAuthGetAccessToken {
        return JiraOAuthGetAccessToken(jiraOAuthProperties.accessTokenUrl).apply {
            consumerKey = jiraOAuthProperties.consumerKey
            signer = getSigner()
            transport = NetHttpTransport()
            temporaryToken = tempToken
            verifier = secret
        }
    }

    /**
     * Initialize JiraOAuthGetTemporaryToken
     * by setting it to use POST method, oob (Out of Band) callback
     * and setting consumer and private keys.
     *
     * @return JiraOAuthGetTemporaryToken request
     */
    fun getTempToken(): JiraOAuthGetTemporaryToken {
        return JiraOAuthGetTemporaryToken(jiraOAuthProperties.requestTokenUrl).apply {
            consumerKey = jiraOAuthProperties.consumerKey
            transport = NetHttpTransport()
            callback = "oob"
            signer = getSigner()
        }
    }

    /**
     * @return OAuthRsaSigner
     */
    private fun getSigner(): OAuthRsaSigner {
        val signer = OAuthRsaSigner()
        signer.privateKey = privateKey
        return signer
    }
}