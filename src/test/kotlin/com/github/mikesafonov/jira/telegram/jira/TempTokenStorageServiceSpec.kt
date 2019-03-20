package com.github.mikesafonov.jira.telegram.jira

import com.github.mikesafonov.jira.telegram.service.jira.TempTokenStorageService
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec

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