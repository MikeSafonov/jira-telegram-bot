package com.github.mikesafonov.jira.telegram.service.templates

import com.github.mikesafonov.jira.telegram.service.templates.freemarker.FreemarkerTemplateService
import freemarker.core.InvalidReferenceException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

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

        When("error in template") {
            val rawTemplate = RawTemplate(
                "my key",
                "*Hello:*\${name2}",
                mapOf("name" to "world")
            )
            Then("throw exception") {
                shouldThrow<InvalidReferenceException> { freemarkerTemplateService.buildMessage(rawTemplate) }
            }
        }

        When("java 8 feature") {
            val rawTemplate = RawTemplate(
                "my key",
                "*Hello:*\${date.format('yyyy MM dd')}",
                mapOf("date" to LocalDate.of(2000, 2, 3))
            )
            Then("return compiled template") {
                val buildMessage = freemarkerTemplateService.buildMessage(rawTemplate)
                buildMessage.markdown shouldBe true
                buildMessage.message shouldBe "*Hello:*2000 02 03"
            }
        }
    }
})
