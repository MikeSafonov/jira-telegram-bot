package com.github.mikesafonov.jira.telegram.service.jira

import com.github.mikesafonov.jira.telegram.dao.Authorization
import com.github.mikesafonov.jira.telegram.service.jira.oauth.JiraOAuthClient
import com.github.mikesafonov.jira.telegram.service.jira.oauth.JiraTempTokenAndAuthorizeUrl
import com.google.api.client.auth.oauth.OAuthParameters
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowExactly
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

/**
 * @author Mike Safonov
 */
class JiraAuthServiceSpec : BehaviorSpec({
    val jiraOauthClient = mockk<JiraOAuthClient>()
    val authorizationService = mockk<AuthorizationService>()
    val tempTokenStorageService = mockk<TempTokenStorageService>()
    val jiraAuthService = JiraAuthService(jiraOauthClient, authorizationService, tempTokenStorageService)

    Given("Jira auth service") {
        When("isAuthorized called and no authorization in database") {
            val id = Gen.long().random().first()
            every { authorizationService.get(id) } returns null
            Then("Return false") {
                jiraAuthService.isAuthorized(id) shouldBe false
            }
        }

        When("isAuthorized called and authorization exist in database") {
            val id = Gen.long().random().first()
            every { authorizationService.get(id) } returns Authorization(id, null, null)
            Then("Return true") {
                jiraAuthService.isAuthorized(id) shouldBe true
            }
        }

        When("Create temporary token called") {
            Then("Save secret to database and put temp token to storage") {
                val id = Gen.long().random().first()
                val tempToken = Gen.string().random().first()
                val secretToken = Gen.string().random().first()
                val url = Gen.string().random().first()
                val token = JiraTempTokenAndAuthorizeUrl(tempToken, secretToken, url)


                every { jiraOauthClient.getAndAuthorizeTemporaryToken() } returns token
                every { tempTokenStorageService.put(any(), any()) } returns Unit
                every { authorizationService.saveSecret(any(), any()) } returns mockk()


                val temporaryToken = jiraAuthService.createTemporaryToken(id)

                temporaryToken shouldBe token

                verify {
                    tempTokenStorageService.put(id, tempToken)
                    authorizationService.saveSecret(id, secretToken)
                }
            }
        }

        When("Create access token called and no authorization in database") {
            Then("Throw JiraAuthorizationException") {
                val id = Gen.long().random().first()
                val verificationCode = Gen.string().random().first()
                every { authorizationService.get(id) } returns null

                shouldThrowExactly<JiraAuthorizationException> { jiraAuthService.createAccessToken(id, verificationCode) }
            }
        }

        When("Create access token called and no temporary token") {
            Then("Throw JiraAuthorizationException") {
                val id = Gen.long().random().first()
                val verificationCode = Gen.string().random().first()
                every { authorizationService.get(id) } returns Authorization(id, null, null)
                every { tempTokenStorageService.get(id) } returns null

                shouldThrowExactly<JiraAuthorizationException> { jiraAuthService.createAccessToken(id, verificationCode) }
            }
        }

        When("Create access token called") {
            Then("Save access token") {
                val id = Gen.long().random().first()
                val verificationCode = Gen.string().random().first()
                val accessToken = Gen.string().random().first()
                val tempToken = Gen.string().random().first()
                val authorization = Authorization(id, null, null)
                val expectedAuthorization = Authorization(id, accessToken, null)

                every { authorizationService.get(id) } returns authorization
                every { tempTokenStorageService.get(id) } returns tempToken
                every { jiraOauthClient.getAccessToken(tempToken, verificationCode) } returns accessToken
                every { authorizationService.save(expectedAuthorization) } returns expectedAuthorization
                every { tempTokenStorageService.remove(id) } returns Unit

                jiraAuthService.createAccessToken(id, verificationCode)

                verify {
                    authorizationService.save(expectedAuthorization)
                    tempTokenStorageService.remove(id)
                }
            }
        }

        When("getOAuthParameters called and no authorization") {
            Then("Throw JiraAuthorizationException") {
                val id = Gen.long().random().first()

                every { authorizationService.get(id) } returns null

                shouldThrowExactly<JiraAuthorizationException> { jiraAuthService.getOAuthParameters(id) }
            }
        }

        When("getOAuthParameters called and no secretToken") {
            Then("Throw JiraAuthorizationException") {
                val id = Gen.long().random().first()
                val accessToken = Gen.string().random().first()
                every { authorizationService.get(id) } returns Authorization(id, accessToken, null)

                shouldThrowExactly<JiraAuthorizationException> { jiraAuthService.getOAuthParameters(id) }
            }
        }

        When("getOAuthParameters called and no accessToken") {
            Then("Throw JiraAuthorizationException") {
                val id = Gen.long().random().first()
                val secretToken = Gen.string().random().first()
                every { authorizationService.get(id) } returns Authorization(id, null, secretToken)

                shouldThrowExactly<JiraAuthorizationException> { jiraAuthService.getOAuthParameters(id) }
            }
        }

        When("getOAuthParameters called ") {
            Then("Return parameters") {
                val id = Gen.long().random().first()
                val secretToken = Gen.string().random().first()
                val accessToken = Gen.string().random().first()
                val parameters = mockk<OAuthParameters>()

                every { authorizationService.get(id) } returns Authorization(id, accessToken, secretToken)
                every { jiraOauthClient.getParameters(accessToken, secretToken)} returns parameters

                jiraAuthService.getOAuthParameters(id) shouldBe parameters
            }
        }
    }
})