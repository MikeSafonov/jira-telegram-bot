package com.github.mikesafonov.jira.telegram.service.jira

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

/**
 * @author Mike Safonov
 */
class TempTokenStorageServiceSpec : BehaviorSpec({
    val tempTokenStorageService = TempTokenStorageService()
    Given("Token storage") {
        When("--") {
            val id = Arb.long().next()
            val token = Arb.string().next()
            Then("--") {
                tempTokenStorageService.get(id) shouldBe null
                tempTokenStorageService.put(id, token)
                tempTokenStorageService.get(id) shouldBe token
                tempTokenStorageService.remove(id)
                tempTokenStorageService.get(id) shouldBe null
            }
        }
    }

})
