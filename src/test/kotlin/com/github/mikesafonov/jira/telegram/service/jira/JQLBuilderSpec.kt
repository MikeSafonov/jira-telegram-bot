package com.github.mikesafonov.jira.telegram.service.jira

import io.kotlintest.matchers.string.beEmpty
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec

class JQLBuilderSpec : BehaviorSpec({
    Given("JQL builder"){
        When("nothing selected"){
            Then("return empty string"){
                JQLBuilder.builder().build() shouldBe beEmpty()
            }
        }

        When("unresolved and assignedTo selected"){
            Then("return expected string"){
                JQLBuilder.builder().unresolved().assignedTo("login").build() shouldBe
                        "resolution = Unresolved and assignee = login "
            }
        }

        When("assignedTo and unresolved and order by date selected"){
            Then("return expected string"){
                JQLBuilder.builder().assignedTo("login").unresolved().orderByDateCreate().build() shouldBe
                        "assignee = login and resolution = Unresolved ORDER BY createdDate"
            }
        }
    }
})