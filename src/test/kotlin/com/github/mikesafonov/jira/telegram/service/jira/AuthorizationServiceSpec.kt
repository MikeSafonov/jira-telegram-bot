package com.github.mikesafonov.jira.telegram.service.jira

import com.github.mikesafonov.jira.telegram.dao.Authorization
import com.github.mikesafonov.jira.telegram.dao.AuthorizationRepository
import io.kotlintest.properties.Gen
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

/**
 * @author Mike Safonov
 */
class AuthorizationServiceSpec : BehaviorSpec({
    val authorizationRepository = mockk<AuthorizationRepository>()
    val authorizationService = AuthorizationService(authorizationRepository)

    Given("Authorization service") {
        When("No authorization in database") {
            val id = Gen.long().random().first()
            val secret = Gen.string().random().first()
            val expectedAuthorization = Authorization(id, null, secret)

            every { authorizationRepository.findById(id) } returns Optional.empty()
            every { authorizationRepository.save(expectedAuthorization) } returns expectedAuthorization

            Then("Create new entity") {

                authorizationService.saveSecret(id, secret)

                verify {
                    authorizationRepository.save(expectedAuthorization)
                }

            }
        }

        When("Authorization in database") {
            val id = Gen.long().random().first()
            val secret = Gen.string().random().first()
            val authorization = Authorization(id, Gen.string().random().first(), Gen.string().random().first())
            val expectedAuthorization = Authorization(id, authorization.accessToken, secret)

            every { authorizationRepository.findById(id) } returns Optional.of(authorization)
            every { authorizationRepository.save(expectedAuthorization) } returns expectedAuthorization

            Then("Update secret") {

                authorizationService.saveSecret(id, secret)

                verify {
                    authorizationRepository.save(expectedAuthorization)
                }

            }
        }
    }

})