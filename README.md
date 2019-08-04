# Jira Telegram Bot
[![codecov](https://codecov.io/gh/MikeSafonov/jira-telegram-bot/branch/master/graph/badge.svg)](https://codecov.io/gh/MikeSafonov/jira-telegram-bot)
![Travis-CI](https://travis-ci.com/MikeSafonov/jira-telegram-bot.svg?branch=master)
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-yellow.svg)](https://conventionalcommits.org)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jira-telegram-bot&metric=alert_status)](https://sonarcloud.io/dashboard?id=MikeSafonov_jira-telegram-bot)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jira-telegram-bot&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=MikeSafonov_jira-telegram-bot)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jira-telegram-bot&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=MikeSafonov_jira-telegram-bot)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jira-telegram-bot&metric=security_rating)](https://sonarcloud.io/dashboard?id=MikeSafonov_jira-telegram-bot)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jira-telegram-bot&metric=bugs)](https://sonarcloud.io/dashboard?id=MikeSafonov_jira-telegram-bot)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jira-telegram-bot&metric=code_smells)](https://sonarcloud.io/dashboard?id=MikeSafonov_jira-telegram-bot)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jira-telegram-bot&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=MikeSafonov_jira-telegram-bot)

[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jira-telegram-bot&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=MikeSafonov_jira-telegram-bot)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jira-telegram-bot&metric=ncloc)](https://sonarcloud.io/dashboard?id=MikeSafonov_jira-telegram-bot)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jira-telegram-bot&metric=sqale_index)](https://sonarcloud.io/dashboard?id=MikeSafonov_jira-telegram-bot)

Jira-telegram-bot is a [Spring Boot](https://github.com/spring-projects/spring-boot) application which handing 
[Jira](https://www.atlassian.com/software/jira) webhook events and sends notifications via 
[Telegram](https://telegram.org) bot.

## Key features

- processing Jira webhook issue events
- notification templating (using [Apache FreeMarker](https://freemarker.apache.org) template engine)
- support several databases (PostgreSQL (v 9+), MySQL (v 5), H2)
- [Jira OAuth](https://developer.atlassian.com/server/jira/platform/oauth/)
- monitoring using [Prometheus](https://prometheus.io)


## Build

### Build from source

You can build application using following command:

    ./gradlew clean build
    
#### Requirements:

JDK >= 1.8

### Unit tests

You can run unit tests using following command:

    ./grdlew test

> gradle task `test` finalized by `pitest` and `testIntegration`

### Mutation tests

You can run mutation tests using following command:

    ./grdlew pitest

You will be able to find pitest report in `build/reports/pitest/` folder.

### Integration tests

You can run integration tests using following command:

    ./grdlew testIntegration

### Running jira-telegram-bot

After the build you will get [fully executable jar archive](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/#packaging-executable-configuring-launch-script)
 
You can run application using following commands:

    java -jar jira-telegram-bot
or

    ./jira-telegram-bot

### Building and running app with Docker and docker-compose

 - For building the application and creation Docker image run
 
        docker-compose build

 - Customise configs with you preferred editor

   place configs in ./config directory 
 
 - Add to  docker-compose.yaml your preferred database service  

 - Run the docker image  

        docker-compose up -d

## Configuration

You can see all the necessary configuration properties in the file [example/application.properties](examples/application.properties)

According to [Spring Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-application-property-files)
you can override default application properties by put custom **application.properties** file in one of the following
locations:

- a `/config` subdirectory of the current directory
- the current directory

#### Custom properties

<dl> 
  <dt>jira.bot.notification.sendToMe</dt>
  <dd>whether the user should receive their self-created events</dd>
    
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

Jira-telegram-bot using [Apache FreeMarker](https://freemarker.apache.org) template engine. All templates by default
stored in jira-telegram-bot database table called **templates**.
Each template must be a message in properly [telegram markdown style](https://core.telegram.org/bots/api#markdown-style).

In [example/templates](examples/templates) folder you can find default jira event templates.

### Jira user registration

To register jira user to receive webhook events you should add corresponding row into jira-telegram-bot database table called **chats**.

You should specify **jira_id** (jira user login) and **telegram_id** (telegram chat id) unique fields.

To find out your telegram chat id you should write simple command "/me" to telegram bot.

## Telegram bot commands

Telegram bot supports following text commands:

- **_/me_** - prints telegram chat id
- **_/jira_login_** - prints attached jira login to this telegram chat id 
- **_/help_** - prints help message

Admin commands:

- **_/users_list_** - prints list of users
- **_/add_user_** *jiraLogin* *telegramId* -  add new user to bot
- **_/remove_user_** *jiraLogin* - remove user from bot

Jira oauth commands:

- **_/auth_** - starts jira authorization
- **_/my_issues_** - shows list of unresolved issues assigned to user

## Jira OAuth 

Please read [Jira OAuth](https://developer.atlassian.com/server/jira/platform/oauth/) to understand how to configure Jira
before using Jira OAuth in jira-telegram-bot.

You must provide next properties to use Jira OAuth in jira-telegram-bot :

<dl>
  <dt>jira.oauth.baseUrl</dt>
  <dd>your jira instance url</dd>
  
  <dt>jira.oauth.authorizationUrl</dt>
  <dd>jira authorization url, {jira.oauth.baseUrl}/plugins/servlet/oauth/authorize</dd>
    
  <dt>jira.oauth.accessTokenUrl</dt>
  <dd>jira access token url, {jira.oauth.baseUrl}/plugins/servlet/oauth/access-token</dd>
  
  <dt>jira.oauth.requestTokenUrl</dt>
  <dd>jira request token url, {jira.oauth.baseUrl}/plugins/servlet/oauth/request-token</dd>
  
  <dt>jira.oauth.consumerKey</dt>
  <dd>consumer key</dd>
  
  <dt>jira.oauth.publicKey</dt>
  <dd>client RSA public key</dd>
  
  <dt>jira.oauth.privateKey</dt>
  <dd>client RSA private key</dd>
</dl>


`/auth` telegram command will be allowed after all properties configured properly .

## Monitoring using [Prometheus](https://prometheus.io)

You can access prometheus metrics by url: 

    {host:port}/actuator/prometheus
    
Jira bot comes with next custom counter metrics:

<dl>
  <dt>jira_bot_event_counter</dt>
  <dd>number of incoming events</dd>
  
  <dt>jira_bot_event_error_counter</dt>
  <dd>number of incoming events with an error</dd>
 </dl>
 
## Contributing

Feel free to contribute. 
New feature proposals and bug fixes should be submitted as GitHub pull requests. 
Fork the repository on GitHub, prepare your change on your forked copy, and submit a pull request.

**IMPORTANT!**
>Before contributing please read about [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0-beta.2/) / [Conventional Commits RU](https://www.conventionalcommits.org/ru/v1.0.0-beta.2/)