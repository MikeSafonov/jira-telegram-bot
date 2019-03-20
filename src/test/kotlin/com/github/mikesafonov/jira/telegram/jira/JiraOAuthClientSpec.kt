package com.github.mikesafonov.jira.telegram.jira

import com.github.mikesafonov.jira.telegram.config.JiraOAuthProperties
import com.github.mikesafonov.jira.telegram.service.jira.oauth.JiraOAuthClient
import com.github.mikesafonov.jira.telegram.service.jira.oauth.JiraOAuthTokenFactory
import com.google.api.client.auth.oauth.OAuthCredentialsResponse
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
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
                val tokenSecretValue = Gen.string().random().first()
                val requestUrl = "http://example.jira/auth"
                val expectedUrl = "$requestUrl?oauth_token=$tokenValue"

                val credentialsResponse = OAuthCredentialsResponse()
                credentialsResponse.token = tokenValue
                credentialsResponse.tokenSecret = tokenSecretValue

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
    }
})