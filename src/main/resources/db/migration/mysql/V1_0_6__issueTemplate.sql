insert into templates(template_key, template) values  ('issue', '*Issue* [${issue.projectName}/${issue.key}](${issueLink})  *was created*
*Date* : `${issue.fields.created.format("HH:mm:ss dd.MM.yyyy")}`
*Summary*: `${issue.fields.summary}`
*Components*: `${issue.componentsAsString}`
*Status*: `${issue.fields.status.name}`
*Author*: `${issue.creatorDisplayName}`
*Reporter*: `${issue.reporterDisplayName}`
*Assignee*: `${issue.assigneeDisplayName}`
<#if issue.containsVersions>
*Versions*: `${issue.versionsAsString}`
</#if>
<#if issue.containsLabels>
*Labels*: `${issue.labelsAsString}`
</#if>
*Description*: `${issue.fields.description}`

<#if issue.containsAttachments>
*Attachments*:
<#list issue.fields.attachment as attach>
 [${attach.filename}](${attach.content})
</#list>
</#if>')
