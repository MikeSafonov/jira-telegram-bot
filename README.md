# Jira Telegram Bot

[![DepShield Badge](https://depshield.sonatype.org/badges/MikeSafonov/jira-telegram-bot/depshield.svg)](https://depshield.github.io)
![Travis-CI](https://travis-ci.com/MikeSafonov/jira-telegram-bot.svg?branch=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jira-telegram-bot&metric=alert_status)](https://sonarcloud.io/dashboard?id=MikeSafonov_jira-telegram-bot)

Jira-telegram-bot is a [Spring Boot](https://github.com/spring-projects/spring-boot) application which handing 
[Jira](https://www.atlassian.com/software/jira) webhook events and sends notifications via 
[Telegram](https://telegram.org) bot.

## Key features

- processing Jira webhook issue events
- notification templating (using [Apache FreeMarker](https://freemarker.apache.org) template engine by default)
- support several databases (PostgreSQL, MySQL, H2)


## Getting started 

### Build

You can build application using following command:

    ./gradlew clean build
    
#### Requirements:

JDK >= 1.8

### Running jira-telegram-bot

After the build you get [fully executable jar archive](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/#packaging-executable-configuring-launch-script)
 
You can run application using following commands:

    java -jar jira-telegram-bot
or

    ./jira-telegram-bot

### Building and running app with Docker and docker-compose

 - For building the application and creation Docker image run
 
    docker-compose build

 - Customise configs with you prefered editor

   place configs in ./config directory 
 
 - Add to  docker-compose.yaml your prefered database service  

 - Run the docker image  

     docker-compose up -d

### Configuration

You can see all the necessary configuration properties in the file [example/application.properties](examples/application.properties)

According to [Spring Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-application-property-files)
you can override default application properties by put custom **application.properties** file in one of the following
locations:

- a `/config` subdirectory of the current directory
- the current directory

#### Custom properties

<dl>
  <dt>jira.bot.template.type</dt>
  <dd>type of template processing engine (default FREEMARKER)</dd>
  
  <dt>jira.bot.notification.sendToMe</dt>
  <dd>is need to notify user about self-created events</dd>
    
  <dt>jira.bot.notification.jiraUrl</dt>
  <dd>jira instance url for building browse link in notification message</dd>
  
  <dt>telegram.bot.token</dt>
  <dd>telegram bot secret token</dd>
  
  <dt>telegram.bot.name</dt>
  <dd>telegram bot name</dd>
  
  <dt>telegram.bot.adminId</dt>
  <dd>id of telegram bot admin</dd>
  
  <dt>telegram.bot.proxyHost</dt>
  <dd>http proxy host</dd>

  <dt>telegram.bot.proxyPort</dt>
  <dd>http proxy port</dd>
  
  <dt>telegram.bot.connectionTimeout</dt>
  <dd>timeout in milliseconds until a connection is established</dd>
  
  <dt>telegram.bot.connectionRequestTimeout</dt>
  <dd>timeout in milliseconds used when requesting a connection</dd>
    
  <dt>telegram.bot.socketTimeout</dt>
  <dd>the socket timeout in milliseconds, which is the timeout for waiting for data  or, put differently, a maximum period inactivity between two consecutive data packets)</dd>
</dl>


### Jira Webhooks

To receive jira webhooks you may to configure your jira instance. [See jira docs](https://developer.atlassian.com/server/jira/platform/webhooks/)

**WARNING!** 
> Jira-telegram-bot supports only **issue** events at the moment

By default jira-telegram-bot process only following issue events:

- issue_created
- issue_updated
- issue_generic
- issue_commented
- issue_assigned

If you want to process any other issue event or change default template you can modify corresponding row in jira-telegram-bot
database table called **templates**.

### Templating

Jira-telegram-bot using [Apache FreeMarker](https://freemarker.apache.org) template engine by default. All templates by default
stored in jira-telegram-bot database table called **templates**.
Each template must be a message in properly [telegram markdown style](https://core.telegram.org/bots/api#markdown-style).

In [example/templates](examples/templates) folder you can find default jira event templates.

If you want to add another templating logic, you can implement **TemplateService** interface.

### Jira user registration

To register jira user to receive webhook events you should add corresponding row into jira-telegram-bot database table called **chats**.

You should specify **jira_id** (jira user login) and **telegram_id** (telegram chat id) unique fields.

To find out your telegram chat id you should write simple command "/me" to telegram bot.

### Telegram bot commands

Telegram bot supports following text commands:

- /me - prints telegram chat id
- /jira_login - prints attached jira login to this telegram chat id 
- /help - prints help message

Admin commands:

- /users_list - prints list of users
- /add_user *jiraLogin* *telegramId* -  add new user to bot
- /remove_user *jiraLogin* - remove user from bot


### Monitoring using [Prometheus](https://prometheus.io)

You can access prometheus metrics by url: 

    {host:port}/actuator/prometheus
    
Jira bot comes with next custom counter metrics:

<dl>
  <dt>jira_bot_event_counter</dt>
  <dd>number of incoming events</dd>
  
  <dt>jira_bot_event_error_counter</dt>
  <dd>number of incoming events with an error</dd>
 </dl>