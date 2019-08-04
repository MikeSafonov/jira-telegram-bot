package com.github.mikesafonov.jira.telegram.service.templates

import com.github.mikesafonov.jira.telegram.service.templates.freemarker.FreemarkerTemplateService
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec

class FreemarkerTemplateServiceSpec : BehaviorSpec({
    Given("freemarker template service") {
        val freemarkerTemplateService = FreemarkerTemplateService()
        When("simple raw template") {
            val rawTemplate = RawTemplate(
                "my key",
                "*Hello:*\${name}",
                mapOf("name" to "world")
            )
            Then("return compiled template") {

                val buildMessage = freemarkerTemplateService.buildMessage(rawTemplate)
                buildMessage.markdown shouldBe true
                buildMessage.message shouldBe "*Hello:*world"
            }
        }
    }
})