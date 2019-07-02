package com.github.mikesafonov.jira.telegram.dto

import com.github.mikesafonov.jira.telegram.generators.*
import io.kotlintest.matchers.string.shouldHaveLength
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec

/**
 * @author Mike Safonov
 */
class IssueSpec : BehaviorSpec({

    Given("Issue") {
        When("No versions") {
            Then("versionsAsString should return empty string") {
                val issue = IssueGen.generateDefault()
                issue.containsVersions shouldBe false
                issue.versionsAsString shouldHaveLength 0
            }
        }

        When("Contain versions") {
            Then("versionsAsString should return expected string") {
                val fixVersions = arrayOf(
                    VersionGen.generateDefault(),
                    VersionGen.generateDefault()
                )
                val issue =
                    IssueGen().generateOne(issueFields = IssueFieldsGen().generateOne(fixVersions = fixVersions))
                issue.versionsAsString shouldBe fixVersions.joinToString { it.name }
                issue.containsVersions shouldBe true
            }
        }

        When("No labels") {
            Then("labelsAsString should return empty string") {
                val issue = IssueGen.generateDefault()
                issue.containsLabels shouldBe false
                issue.labelsAsString shouldHaveLength 0
            }
        }

        When("Contain labels") {
            Then("labelsAsString should return expected string") {
                val labels = arrayOf(
                    Gen.string().random().first(),
                    Gen.string().random().first()
                )
                val issue = IssueGen().generateOne(issueFields = IssueFieldsGen().generateOne(labels = labels))
                issue.containsLabels shouldBe true
                issue.labelsAsString shouldBe labels.joinToString { it }
            }
        }

        When("No components") {
            Then("componentsAsString should return empty string") {
                val issue = IssueGen.generateDefault()
                issue.componentsAsString shouldHaveLength 0
            }
        }

        When("Contain components") {
            Then("componentsAsString should return expected string") {
                val components = arrayOf(
                    JiraComponentGen.generateDefault(),
                    JiraComponentGen.generateDefault()
                )
                val issue = IssueGen().generateOne(issueFields = IssueFieldsGen().generateOne(components = components))
                issue.componentsAsString shouldBe components.joinToString { it.name }
            }
        }

        When("With users") {
            Then("Issue should contains expected users names") {
                val issue = IssueGen.generateDefault()
                issue.creatorName shouldBe issue.fields.creator.name
                issue.assigneeName shouldBe issue.fields.assignee?.name
                issue.reporterName shouldBe issue.fields.reporter?.name

                issue.creatorDisplayName shouldBe issue.fields.creator.displayName
                issue.assigneeDisplayName shouldBe issue.fields.assignee?.displayName
                issue.reporterDisplayName shouldBe issue.fields.reporter?.displayName
            }
        }

        When("With attachments") {
            Then("Issue should contains expected attachments") {
                val issue = IssueGen().generateOne(
                    issueFields = IssueFieldsGen().generateOne(
                        attachment = arrayOf(
                            AttachmentGen.generateDefault(),
                            AttachmentGen.generateDefault()
                        )
                    )
                )
                issue.containsAttachments shouldBe true
            }
        }
    }
})