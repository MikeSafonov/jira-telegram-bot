package com.github.mikesafonov.jira.telegram.service.jira

import com.google.api.client.auth.oauth.OAuthGetTemporaryToken

class JiraOAuthGetTemporaryToken : OAuthGetTemporaryToken {
    constructor(authorizationUrl : String) : super(authorizationUrl) {
        usePost = true
    }
}