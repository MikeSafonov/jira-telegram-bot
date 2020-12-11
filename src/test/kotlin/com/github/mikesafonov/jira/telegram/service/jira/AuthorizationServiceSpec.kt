package com.github.mikesafonov.jira.telegram.service.jira

import com.github.mikesafonov.jira.telegram.dao.Authorization
import com.github.mikesafonov.jira.telegram.dao.AuthorizationRepository
import com.github.mikesafonov.jira.telegram.service.AuthorizationService
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

/**
 * @author Mike Safonov
 */
class AuthorizationServiceSpec : BehaviorSpec({
    val authorizationRepository = mockk<AuthorizationRepository>()
    val authorizationService =
        AuthorizationService(authorizationRepository)

    Given("Authorization service") {
        When("No authorization in database") {
            val id = Arb.long().next()
            val secret = Arb.string().next()
            val expectedAuthorization = Authorization(id, null, secret)

            every { authorizationRepository.findById(id) } returns Optional.empty()
            every { authorizationRepository.save(expectedAuthorization) } returns expectedAuthorization

            Then("Create new entity") {

                val savedAuthorization = authorizationService.saveSecret(id, secret)

                savedAuthorization shouldBeSameInstanceAs expectedAuthorization
                verify {
                    authorizationRepository.save(expectedAuthorization)
                }

            }
        }

        When("Authorization in database") {
            val id = Arb.long().next()
            val secret = Arb.string().next()
            val authorization = Authorization(id, Arb.string().next(), Arb.string().next())
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
