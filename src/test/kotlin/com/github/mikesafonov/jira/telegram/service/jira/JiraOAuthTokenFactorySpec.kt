package com.github.mikesafonov.jira.telegram.service.jira

import com.github.mikesafonov.jira.telegram.config.JiraOAuthProperties
import com.github.mikesafonov.jira.telegram.service.jira.oauth.JiraOAuthTokenFactory
import com.github.mikesafonov.jira.telegram.service.jira.oauth.KeyReader
import com.google.api.client.auth.oauth.OAuthRsaSigner
import com.google.api.client.http.GenericUrl
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.mockk.every
import io.mockk.mockk
import java.net.URL

/**
 * @author Mike Safonov
 */
class JiraOAuthTokenFactorySpec : BehaviorSpec({

    val jiraOAuthProperties = mockk<JiraOAuthProperties>()
    val keyReader = mockk<KeyReader>()
    every { jiraOAuthProperties.privateKey } returns Arb.string().next()
    every { keyReader.readPrivateRsa(any()) } returns mockk()
    val jiraOAuthTokenFactory = JiraOAuthTokenFactory(jiraOAuthProperties, keyReader)

    Given("Jira oauth token factory") {
        When("Call get temp token") {
            val requestUrl = "http://example.jira/request"
            val consumerKey = Arb.string().next()
            every { jiraOAuthProperties.requestTokenUrl } returns requestUrl
            every { jiraOAuthProperties.consumerKey } returns consumerKey
            Then("Create token request with expected parameters") {

                val url = URL(requestUrl)
                val token = jiraOAuthTokenFactory.getTempToken()

                token.host = url.host
                token.port = url.port
                token.scheme = url.protocol
                token.pathParts = GenericUrl.toPathParts(url.path)
                token.consumerKey shouldBe consumerKey
                token.signer::class shouldBeSameInstanceAs  OAuthRsaSigner::class
            }
        }

        When("Call get access token") {
            val requestUrl = "http://example.jira/access"
            val consumerKey = Arb.string().next()
            val secretToken = Arb.string().next()
            val tempToken = Arb.string().next()
            every { jiraOAuthProperties.accessTokenUrl } returns requestUrl
            every { jiraOAuthProperties.consumerKey } returns consumerKey
            Then("Create token request with expected parameters") {
                val url = URL(requestUrl)
                val token = jiraOAuthTokenFactory.getAccessToken(secretToken, tempToken)

                token.host = url.host
                token.port = url.port
                token.scheme = url.protocol
                token.pathParts = GenericUrl.toPathParts(url.path)
                token.consumerKey shouldBe consumerKey
                token.temporaryToken shouldBe tempToken
                token.verifier shouldBe secretToken
                token.signer::class shouldBeSameInstanceAs OAuthRsaSigner::class
            }
        }
    }

})
