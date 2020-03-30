package com.github.mikesafonov.jira.telegram.service.jira

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.properties.Gen
import io.kotest.properties.long
import io.kotest.properties.string

/**
 * @author Mike Safonov
 */
class TempTokenStorageServiceSpec : BehaviorSpec({
    val tempTokenStorageService = TempTokenStorageService()
    Given("Token storage") {
        When("") {
            val id = Gen.long().random().first()
            val token = Gen.string().random().first()
            Then("") {
                tempTokenStorageService.get(id) shouldBe null
                tempTokenStorageService.put(id, token)
                tempTokenStorageService.get(id) shouldBe token
                tempTokenStorageService.remove(id)
                tempTokenStorageService.get(id) shouldBe null
            }
        }
    }

})
