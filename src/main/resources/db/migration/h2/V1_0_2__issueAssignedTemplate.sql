insert into templates(template_key, template)
values ('issue_assigned', '*Issue* [${event.projectName}/${event.issue.key}](${issueLink}) *was assigned*
*Date* : `${event.eventDate.format("HH:mm:ss dd.MM.yyyy")}`
*Summary*: `${event.issue.fields.summary}`
*Status*: `${event.issue.fields.status.name}`
*Updated by*: `${event.user.displayName}`
*Assignee*: `${event.issue.assigneeDisplayName}`
*Description*: `${event.issue.fields.description}`')