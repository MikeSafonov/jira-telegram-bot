package com.github.mikesafonov.jira.telegram

import com.github.mikesafonov.jira.telegram.dto.WebHookEvent
import com.github.mikesafonov.jira.telegram.generators.EventGen
import io.kotlintest.matchers.string.shouldHaveLength
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec

class EventSpec : BehaviorSpec({
    Given("Event"){
        When("Contains issue"){
            Then("Should contain project name"){
                val event = EventGen.generateDefault()
                event.projectName shouldBe event.issue?.fields?.project?.name
            }
        }
        When("Not contain issue"){
            Then("Should not contain project name"){
                val event = EventGen().generateOne(issue = null)
                event.projectName shouldHaveLength 0
            }
        }

        When("WebHookEvent field is one of issue types"){
            Then("Should isIssueEvent be true"){

                WebHookEvent.values().forEach {
                    val isIssueEventExpected = it.isIssueEvent()
                    val event = EventGen().generateOne(webHookEvent = it)
                    event.isIssueEvent shouldBe isIssueEventExpected
                }
            }
        }
    }
})
