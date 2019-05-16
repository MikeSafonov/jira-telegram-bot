package com.github.mikesafonov.jira.telegram.service.jira.oauth

import com.github.mikesafonov.jira.telegram.config.conditional.ConditionalOnJiraOAuth
import com.google.api.client.util.Base64
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec

/**
 * @author Mike Safonov
 */
@Service
@ConditionalOnJiraOAuth
class KeyReader {

    fun readPrivateRsa(privateKey: String): PrivateKey {
        val privateBytes = Base64.decodeBase64(privateKey)
        val keySpec = PKCS8EncodedKeySpec(privateBytes)
        val kf = KeyFactory.getInstance("RSA")
        return kf.generatePrivate(keySpec)
    }
}