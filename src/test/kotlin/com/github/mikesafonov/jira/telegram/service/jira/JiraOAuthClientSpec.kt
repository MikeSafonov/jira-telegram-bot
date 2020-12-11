package com.github.mikesafonov.jira.telegram.service.jira

import com.github.mikesafonov.jira.telegram.config.JiraOAuthProperties
import com.github.mikesafonov.jira.telegram.service.jira.oauth.JiraOAuthClient
import com.github.mikesafonov.jira.telegram.service.jira.oauth.JiraOAuthTokenFactory
import com.google.api.client.auth.oauth.OAuthCredentialsResponse
import com.google.api.client.auth.oauth.OAuthParameters
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.mockk.every
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class JiraOAuthClientSpec : BehaviorSpec({
    val factory = mockk<JiraOAuthTokenFactory>()
    val properties = mockk<JiraOAuthProperties>()
    val jiraOAuthClient = JiraOAuthClient(factory, properties)

    Given("Jira oauth client") {
        When("Call create and authorize temp token") {
            Then("Generate expected token, secret and url") {
                val tokenValue = "aasdsHasdmnnqweqhQWHh"
                val tokenSecretValue = Arb.string().next()
                val requestUrl = "http://example.jira/auth"
                val expectedUrl = "$requestUrl?oauth_token=$tokenValue"

                val credentialsResponse = OAuthCredentialsResponse().apply {
                    token = tokenValue
                    tokenSecret = tokenSecretValue
                }

                every { properties.authorizationUrl } returns requestUrl
                every { factory.getTempToken() } returns mockk {
                    every { execute() } returns credentialsResponse
                }

                val jiraTempTokenAndAuthorizeUrl = jiraOAuthClient.getAndAuthorizeTemporaryToken()

                jiraTempTokenAndAuthorizeUrl.token shouldBe tokenValue
                jiraTempTokenAndAuthorizeUrl.secret shouldBe tokenSecretValue
                jiraTempTokenAndAuthorizeUrl.url shouldBe expectedUrl
            }
        }

        When("Get access token") {
            Then("Generate access token") {
                val tokenValue = Arb.string().next()
                val tokenSecretValue = Arb.string().next()
                val accessToken = Arb.string().next()
                val response = OAuthCredentialsResponse().apply {
                    token = accessToken
                    tokenSecret = tokenSecretValue
                }

                every { factory.getAccessToken(tokenSecretValue, tokenValue) } returns mockk {
                    every { execute() } returns response
                }

                jiraOAuthClient.getAccessToken(tokenValue, tokenSecretValue) shouldBe accessToken
            }
        }

        When("Get oauth parameters") {
            Then("Generate oauth parameters") {
                val tokenValue = Arb.string().next()
                val tokenSecretValue = Arb.string().next()
                val parameters = mockk<OAuthParameters>()

                every { factory.getAccessToken(tokenSecretValue, tokenValue) } returns mockk {
                    every { createParameters() } returns parameters
                }

                jiraOAuthClient.getParameters(tokenValue, tokenSecretValue) shouldBe parameters
            }
        }
    }
})
