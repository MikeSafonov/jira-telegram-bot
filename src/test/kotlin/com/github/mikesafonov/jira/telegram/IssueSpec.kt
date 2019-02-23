package com.github.mikesafonov.jira.telegram

import com.github.mikesafonov.jira.telegram.generators.*
import io.kotlintest.matchers.string.shouldHaveLength
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

/**
 * @author Mike Safonov
 */
class IssueSpec : StringSpec({
    "Issue without versions should return empty string when versionsAsString called"{
        val issue = IssueGen.generateDefault()
        issue.containsVersions shouldBe false
        issue.versionsAsString shouldHaveLength 0
    }

    "Issue with versions should return expected string when versionsAsString called"{
        val fixVersions = arrayOf(
            VersionGen.generateDefault(),
            VersionGen.generateDefault()
        )
        val issue = IssueGen().generateOne(issueFields = IssueFieldsGen().generateOne(fixVersions = fixVersions))
        issue.versionsAsString shouldBe fixVersions.joinToString { it.name }
        issue.containsVersions shouldBe true
    }

    "Issue without labels should return empty string when labelsAsString called"{
        val issue = IssueGen.generateDefault()
        issue.containsLabels shouldBe false
        issue.labelsAsString shouldHaveLength 0
    }

    "Issue with labels should return expected string when labelsAsString called"{
        val labels = arrayOf(
            Gen.string().random().first(),
            Gen.string().random().first()
        )
        val issue = IssueGen().generateOne(issueFields = IssueFieldsGen().generateOne(labels = labels))
        issue.containsLabels shouldBe true
        issue.labelsAsString shouldBe labels.joinToString { it }
    }

    "Issue without components should return empty string when componentsAsString called"{
        val issue = IssueGen.generateDefault()
        issue.componentsAsString shouldHaveLength 0
    }

    "Issue with components should return expected string when componentsAsString called"{
        val components = arrayOf(
            JiraComponentGen.generateDefault(),
            JiraComponentGen.generateDefault()
        )
        val issue = IssueGen().generateOne(issueFields = IssueFieldsGen().generateOne(components = components))
        issue.componentsAsString shouldBe components.joinToString { it.name }
    }

    "Issue should return expected users names"{
        val issue = IssueGen.generateDefault()
        issue.creatorName shouldBe issue.fields.creator.name
        issue.assigneeName shouldBe issue.fields.assignee?.name
        issue.reporterName shouldBe issue.fields.reporter?.name

        issue.creatorDisplayName shouldBe issue.fields.creator.displayName
        issue.assigneeDisplayName shouldBe issue.fields.assignee?.displayName
        issue.reporterDisplayName shouldBe issue.fields.reporter?.displayName
    }

    "Issue should contain attachments"{
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

})