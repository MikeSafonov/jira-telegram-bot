package com.github.mikesafonov.jira.telegram

import com.github.mikesafonov.jira.telegram.generators.IssueFieldsGen
import com.github.mikesafonov.jira.telegram.generators.IssueGen
import com.github.mikesafonov.jira.telegram.generators.VersionGen
import io.kotlintest.matchers.string.shouldHaveLength
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class IssueSpec : StringSpec({
    "Issue without versions should return empty string when versionsAsString called"{
        val issue = IssueGen.generateDefault()
        issue.versionsAsString shouldHaveLength 0
    }

    "Issue with versions should return expected string when versionsAsString called"{
        val fixVersions = arrayOf(
            VersionGen.generateDefault(),
            VersionGen.generateDefault()
        )
        val issue = IssueGen().generateOne(issueFields = IssueFieldsGen().generateOne(fixVersions = fixVersions))
        issue.versionsAsString shouldBe fixVersions.joinToString { it.name }
    }

})