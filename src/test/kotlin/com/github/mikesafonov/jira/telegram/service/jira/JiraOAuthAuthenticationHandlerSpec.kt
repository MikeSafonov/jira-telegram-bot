package com.github.mikesafonov.jira.telegram.service.jira

import com.atlassian.httpclient.api.Request
import com.github.mikesafonov.jira.telegram.service.jira.oauth.JiraOAuthAuthenticationHandler
import com.google.api.client.auth.oauth.OAuthParameters
import com.google.api.client.http.GenericUrl
import io.kotlintest.specs.BehaviorSpec
import io.mockk.*
import java.net.URI

class JiraOAuthAuthenticationHandlerSpec : BehaviorSpec({
    Given("JiraOAuthAuthenticationHandler instance") {
        When("GET request comes") {
            Then("oauth authorization header was added") {
                val myUri = URI("http://my.jira.com")
                val getMethod = Request.Method.GET
                val genericUrl = GenericUrl(myUri)
                val expectedHeader = "Header"
                val oAuthParameters = mockk<OAuthParameters>{
                    every { computeNonce() } just Runs
                    every { computeTimestamp() } just Runs
                    every { computeSignature("GET", genericUrl)} just Runs
                    every { authorizationHeader } returns expectedHeader
                }
                val jiraOAuthAuthenticationHandler = JiraOAuthAuthenticationHandler(oAuthParameters)

                val request = mockk<Request> {
                    every { method } returns getMethod
                    every { uri } returns myUri
                }
                val requestBuilder = mockk<Request.Builder> {
                    every { build() } returns request
                    every { setHeader(any(), any())} returns this
                }

                jiraOAuthAuthenticationHandler.configure(requestBuilder)

                verify {
                    oAuthParameters.computeNonce()
                    oAuthParameters.computeTimestamp()
                    oAuthParameters.computeSignature("GET", genericUrl)
                    requestBuilder.setHeader("Authorization", expectedHeader)
                }
            }
        }
    }
})