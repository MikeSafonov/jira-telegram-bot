package com.github.mikesafonov.jira.telegram.service.templates

import com.github.mikesafonov.jira.telegram.dao.TemplateRepository
import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.dto.IssueEventTypeName
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class TemplateResolverServiceSpec : BehaviorSpec({
    Given("template resolver") {
        val templateRepository = mockk<TemplateRepository>()
        val templateResolverService = TemplateResolverService(templateRepository)
        When("events issue type is null") {
            val event = mockk<Event> {
                every { issueEventTypeName } returns null
            }
            Then("return null raw template") {
                templateResolverService.resolve(event, emptyMap()) shouldBe null
            }
        }

        When("no event issue type template in database") {
            val event = mockk<Event> {
                every { issueEventTypeName } returns IssueEventTypeName.ISSUE_ASSIGNED
            }
            every { templateRepository.findByKey(IssueEventTypeName.ISSUE_ASSIGNED.toString().toLowerCase()) } returns null
            Then("return null raw template") {
                templateResolverService.resolve(event, emptyMap()) shouldBe null
            }
        }

        When("event issue type template exist in database") {
            val event = mockk<Event> {
                every { issueEventTypeName } returns IssueEventTypeName.ISSUE_ASSIGNED
            }
            val tmpl = "Some template"
            every { templateRepository.findByKey(IssueEventTypeName.ISSUE_ASSIGNED.toString().toLowerCase()) } returns mockk {
                every { template } returns tmpl
            }
            val emptyParameters: Map<String, Any> = emptyMap()
            Then("return expected raw template") {
                val rawTemplate = templateResolverService.resolve(event, emptyMap())!!
                rawTemplate.template shouldBe tmpl
                rawTemplate.templateKey shouldBe IssueEventTypeName.ISSUE_ASSIGNED.toString().toLowerCase()
                rawTemplate.parameters shouldBe emptyParameters
            }
        }
    }
})
