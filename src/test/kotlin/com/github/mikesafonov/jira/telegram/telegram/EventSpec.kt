package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.service.telegram.Event
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec

/**
 * @author Mike Safonov
 */
class EventSpec : BehaviorSpec({
    Given("Event") {
        When("Unknown command") {
            Then("Return UNKNOWN") {
                Event.parse(Gen.string().random().first()) shouldBe Event.UNKNOWN
            }
        }

        When("Help command"){
            Then("Return HELP_REQUEST"){
                Event.parse("/help") shouldBe Event.HELP_REQUEST
            }
        }

        When("Me command"){
            Then("Return ME_REQUEST"){
                Event.parse("/me") shouldBe Event.ME_REQUEST
            }
        }

        When("Login command"){
            Then("Return LOGIN_REQUEST"){
                Event.parse("/jira_login") shouldBe Event.LOGIN_REQUEST
            }
        }

        When("Users list command"){
            Then("Return USERS_REQUEST"){
                Event.parse("/users_list") shouldBe Event.USERS_REQUEST
            }
        }

        When("Add user command"){
            Then("Return ADD_USER_REQUEST"){
                Event.parse("/add_user") shouldBe Event.ADD_USER_REQUEST
            }
        }

        When("Remove user command"){
            Then("Return REMOVE_USER_REQUEST"){
                Event.parse("/remove_user") shouldBe Event.REMOVE_USER_REQUEST
            }
        }

        When("Auth command"){
            Then("Return AUTH_REQUEST"){
                Event.parse("/auth") shouldBe Event.AUTH_REQUEST
            }
        }
    }
})