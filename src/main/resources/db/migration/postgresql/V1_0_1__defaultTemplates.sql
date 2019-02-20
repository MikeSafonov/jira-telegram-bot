insert into templates(template_key, template) values ('issue_commented', '*Issue* [${event.projectName}/${event.issue.key}](${issueLink}) *was commented*
*Date* : `${event.eventDate.format("HH:mm:ss dd.MM.yyyy")}`
*Summary*: `${event.issue.fields.summary}`
*Status*: `${event.issue.fields.status.name}`
*Comment*:
_${event.comment.author.displayName}_ said:
`${event.comment.body}`'), ('issue_created', '*Issue* [${event.projectName}/${event.issue.key}](${issueLink})  *was created*
*Date* : `${event.eventDate.format("HH:mm:ss dd.MM.yyyy")}`
*Summary*: `${event.issue.fields.summary}`
*Components*: `${event.issue.componentsAsString}`
*Status*: `${event.issue.fields.status.name}`
*Author*: `${event.issue.creatorDisplayName}`
*Reporter*: `${event.issue.reporterDisplayName}`
*Assignee*: `${event.issue.assigneeDisplayName}`
<#if event.issue.containsVersions>
*Versions*: `${event.issue.versionsAsString}`
</#if>
<#if event.issue.containsLabels>
*Labels*: `${event.issue.labelsAsString}`
</#if>
*Description*: `${event.issue.fields.description}`

<#if event.issue.containsAttachments>
*Attachments*:
<#list event.issue.fields.attachment as attach>
 [${attach.filename}](${attach.content})
</#list>
</#if>'), ('issue_generic','*Issue* [${event.projectName}/${event.issue.key}](${issueLink}) *was updated*
*Date* : `${event.eventDate.format("HH:mm:ss dd.MM.yyyy")}`
*Summary*: `${event.issue.fields.summary}`
*Updated by*: `${event.user.displayName}`
<#list event.changelog.items as log>
<#if log.statusChanged>
*Status*: `${log.fromString}` → `${log.newString}`
</#if>
<#if log.assigneeChanged>
*Assignee*: `${log.fromString}` → `${log.newString}`
</#if>
</#list>'), ('issue_updated', '*Issue* [${event.projectName}/${event.issue.key}](${issueLink}) *was updated*
*Date* : `${event.eventDate.format("HH:mm:ss dd.MM.yyyy")}`
*Summary*: `${event.issue.fields.summary}`
*Status*: `${event.issue.fields.status.name}`
*Updated by*: `${event.user.displayName}`
*Changelog*:
<#list event.changelog.items as log>
*${log.field}* :
<#if log.changed>
`${log.fromString}` → `${log.newString}`
<#elseif log.added>
value was added `${log.newString}`
<#else>
value `${log.fromString}` was removed
</#if>
</#list>')