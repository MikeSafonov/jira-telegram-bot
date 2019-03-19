package com.github.mikesafonov.jira.telegram.service.jira.oauth

import com.github.mikesafonov.jira.telegram.config.JiraOAuthProperties
import com.google.api.client.auth.oauth.OAuthRsaSigner
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.util.Base64
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec

@Service
class JiraOAuthTokenFactory(private val jiraOAuthProperties: JiraOAuthProperties) {
    private final val privateKey: PrivateKey

    init {
        privateKey = getPrivateKey(jiraOAuthProperties.privateKey)
    }

    /**
     * Initialize JiraOAuthGetAccessToken
     * by setting it to use POST method, secret, request token
     * and setting consumer and private keys.
     *
     * @param tempToken    request token
     * @param secret      secret (verification code provided by JIRA after request token authorization)
     * @return JiraOAuthGetAccessToken request
     */
    fun getAccessToken(secret: String, tempToken: String
    ): JiraOAuthGetAccessToken {
        val token =
            JiraOAuthGetAccessToken(jiraOAuthProperties.accessTokenUrl)
        return with(token) {
            consumerKey = jiraOAuthProperties.consumerKey
            signer = getSigner()
            transport = NetHttpTransport()
            temporaryToken = tempToken
            verifier = secret
            this
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
        val token =
            JiraOAuthGetTemporaryToken(jiraOAuthProperties.requestTokenUrl)
        return with(token) {
            consumerKey = jiraOAuthProperties.consumerKey
            transport = NetHttpTransport()
            callback = "oob"
            signer = getSigner()
            this
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

    /**
     * Creates PrivateKey from string
     *
     * @param privateKey private key in PKCS8 format
     * @return private key
     */
    private fun getPrivateKey(privateKey: String): PrivateKey {
        val privateBytes = Base64.decodeBase64(privateKey)
        val keySpec = PKCS8EncodedKeySpec(privateBytes)
        val kf = KeyFactory.getInstance("RSA")
        return kf.generatePrivate(keySpec)
    }
}